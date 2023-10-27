package com.example.demo.Service.Comments;

import com.example.demo.Mapper.Repository.CommentRepository;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.Entity.PostComment;
import com.example.demo.Model.VO.*;
import com.example.demo.Service.IP.IpAddressService;
import com.example.demo.Service.MQ.MQSender;
import com.example.demo.Service.Message.MessageService;
import com.example.demo.Service.Posts.PostInfoService;
import com.example.demo.Service.Redis.RedisService;
import com.example.demo.Service.Replies.ReplyService;
import com.example.demo.Service.UserSettings.CommentOnPostMentionService;
import com.example.demo.Service.UserSettings.UserSettingService;
import com.example.demo.Util.DateTimeConverter;
import com.example.demo.Util.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;

@Service
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    //For response entity map
    private static final String COMMENT = "comment";
    private static final String REPLY = "reply";
    private static final String MESSAGE_MENTION_KEY = "UNREAD:";
    private static final Integer TypeId = 5;
    @Autowired
    private CommentRepository commentRepository;
    @Lazy
    @Autowired
    private ReplyService replyService;
    @Autowired
    private IpAddressService ipAddressService;
    @Autowired
    private PostInfoService postInfoService;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private MessageService messageService;
    @Autowired
    private CommentOnPostMentionService commentOnPostMentionService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserSettingService userSettingService;
    @Autowired
    private CommentInfoService commentInfoService;

    @Transactional
    public CommentSavedVO SaveComment(CommentReplyDTO commentReplyDTO) {
        logger.info("Saving comment: {}");
        try {
            long commentId = Snowflake.generateUniqueId();
            commentReplyDTO.setCommentId(commentId);
            commentReplyDTO.setCreatedAt(Instant.now());
            commentReplyDTO.setTypeId(TypeId);
            PostComment postComment = new PostComment();
            postComment.setId(commentId);
            postComment.setContent(commentReplyDTO.getContent());
            postComment.setFromUid(commentReplyDTO.getFromUid());
            postComment.setPostId(commentReplyDTO.getPostId());
            postComment.setCreatedAt(commentReplyDTO.getCreatedAt());
            postComment.setModifiedAt(commentReplyDTO.getCreatedAt());
            if (commentRepository.save(postComment) != null) {
                logger.info("Comment saved successfully: {}");
                //set comment reply ip address
                ipAddressService.SetCommentIpAddress(commentReplyDTO);
                //set comment like count
                commentInfoService.UpdateCommentLikeCount(commentReplyDTO.getCommentId(), 0);
                //send comment count message to ActiveMQ
                mqSender.SendCommentCountMessage(commentReplyDTO);
                //Set mention message after saved comment if the user is not the author of the post
                boolean sameUser = commentReplyDTO.getFromUid().equals(commentReplyDTO.getToUid());
                boolean commentOnPostMention = userSettingService.CheckCommentOnPostMention(commentReplyDTO.getToUid());
                if (!sameUser && commentOnPostMention) {
                    logger.info("FromUid is not equal to ToUid and comment on post mention setting is on");
                    messageService.SaveMessage(commentReplyDTO, commentReplyDTO.getToUid());
                    if(redisService.CacheExists(MESSAGE_MENTION_KEY + commentReplyDTO.getToUid())){
                        mqSender.SendMentionMessage(commentReplyDTO.getToUid());
                        logger.info("Sent comment on post mention message to MQ");
                    } else {
                        //
                    }
                } else {
                    logger.info("FromUid is equal to ToUid or comment on post mention setting is off");
                }
                return TransferToVO(commentReplyDTO);
            } else {
                logger.info("Failed to save comment: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to set comment: {}", e.getMessage(), e);
        }
        return null;
    }

    private static CommentSavedVO TransferToVO(CommentReplyDTO commentReplyDTO) throws ParseException {
        CommentSavedVO commentSavedVO = new CommentSavedVO();
        commentSavedVO.setCommentId(commentReplyDTO.getCommentId().toString());
        commentSavedVO.setPostId(commentReplyDTO.getPostId().toString());
        String formattedDateTime = DateTimeConverter.DateTimeConvertFromInstant(Instant.now());
        commentSavedVO.setCreatedAt(formattedDateTime);
        return commentSavedVO;
    }

    public List<Map<String, Object>> GetAllCommentsByPostId(Long postId, Long userId) {
        logger.info("Getting all comments by post id: {}", postId);
        return commentRepository.findCommentsByPostId(postId, userId);
    }

    public List<Map<String, Object>> GetAllCommentsAndReplies(Long postId, Long userId) throws ParseException {
        logger.info("Getting all comments and replies");
        //get all comments by post id
        List<Map<String, Object>> commentsList = GetAllCommentsByPostId(postId, userId);
        List<Map<String, Object>> res = new ArrayList<>();
        //get all comment ids from all comments for getting all replies with same comment ids
        if (commentsList.isEmpty()) {
            logger.info("No comments found");
            return Collections.emptyList();
        } else {
            logger.info("Comments found");
            List<Long> commentIds = new ArrayList<>();
            for (Map<String, Object> comment : commentsList) {
                commentIds.add(((BigInteger) comment.get("comment_id")).longValue());
            }
            //get all replies by comment ids
            List<Map<String, Object>> repliesList = replyService.GetRepliesByCommentId(commentIds, userId);
            if (repliesList.isEmpty()) {
                logger.info("No replies found");
                for (Map<String, Object> comment : commentsList) {
                    ShowCommentVO showCommentVO = CreateCommentMap(comment);
                    List<Map<String, Object>> combinedList = new ArrayList<>();
                    Map<String, Object> map = new HashMap<>();
                    map.put(COMMENT, showCommentVO);
                    map.put(REPLY, Collections.emptyList());
                    res.add(map);
                }
            } else {
                logger.info("Replies found");
                //final result list
                //traverse all comments and pick out replies with same comment id
                logger.info("comment list:{}", commentsList);
                for (Map<String, Object> comment : commentsList) {
                    //get comment id
                    Long commentId = ((BigInteger) comment.get("comment_id")).longValue();
                    List<ShowReplyVO> replies = new ArrayList<>();
                    //traverse all replies and pick out replies with same comment id got above
                    for (Map<String, Object> reply : repliesList) {
                        //set reply map if the comment id is same as the one got above
                        Long commentId2 = ((BigInteger) reply.get("comment_id")).longValue();
                        if (commentId.equals(commentId2)) {
                            ShowReplyVO showReplyVO = CreateReplyMap(reply);
                            replies.add(showReplyVO);
                        } else {
                            //continue;
                        }
                    }
                    ShowCommentVO showCommentVO = CreateCommentMap(comment);
                    Map<String, Object> map = new HashMap<>();
                    map.put(COMMENT, showCommentVO);
                    map.put(REPLY, replies);
                    res.add(map);
                }
            }
            return res;
        }
    }

    private static ShowCommentVO CreateCommentMap(Map<String, Object> comment) throws ParseException {
        logger.info("Creating comment map");
//        pc.comment_id, p.post_id, pc.from_uid, ui.username, ui.avatar_url, pc.content, ulc.like_status, pc.created_at
        ShowCommentVO showCommentVO = new ShowCommentVO();
        showCommentVO.setCommentId(String.valueOf(comment.get("comment_id")));
        showCommentVO.setPostId(String.valueOf(comment.get("post_id")));
        showCommentVO.setFromUid(String.valueOf(comment.get("from_uid")));
        showCommentVO.setUsername(String.valueOf(comment.get("username")));
        showCommentVO.setFromAvatarURL(String.valueOf(comment.get("fm_avatar_url")));
        showCommentVO.setContent(String.valueOf(comment.get("content")));
        showCommentVO.setLikeStatus(String.valueOf(comment.get("like_status")));
        showCommentVO.setLikeNum(String.valueOf(comment.get("like_num")));
        String formattedDateTime = DateTimeConverter.DateTimeConvertFromString(String.valueOf(comment.get("created_at")));
        showCommentVO.setCreatedAt(formattedDateTime);

        return showCommentVO;
    }

    private static ShowReplyVO CreateReplyMap(Map<String, Object> reply) throws ParseException {
        logger.info("Creating reply map");

        ShowReplyVO showReplyVO = new ShowReplyVO();
        showReplyVO.setReplyId(String.valueOf(reply.get("reply_id")));
        showReplyVO.setCommentId(String.valueOf(reply.get("comment_id")));
        showReplyVO.setToReplyId(String.valueOf(reply.get("to_reply_id")));
        showReplyVO.setFromUid(String.valueOf(reply.get("from_uid")));
        showReplyVO.setFromAvatarURL(String.valueOf(reply.get("fm_avatar_url")));
        showReplyVO.setFromUsername(String.valueOf(reply.get("fm_username")));
        showReplyVO.setToUid(String.valueOf(reply.get("to_uid")));
        showReplyVO.setToUsername(String.valueOf(reply.get("to_username")));
        showReplyVO.setContent(String.valueOf(reply.get("content")));
        showReplyVO.setLikeStatus(String.valueOf(reply.get("like_status")));
        showReplyVO.setLikeNum(String.valueOf(reply.get("like_num")));
        String formattedDateTime = DateTimeConverter.DateTimeConvertFromString(String.valueOf(reply.get("created_at")));
        showReplyVO.setCreatedAt(formattedDateTime);

        return showReplyVO;
    }

    public List<NewestCommentVO> GetNewestComments() {
        logger.info("Getting newest comments");
        try {
            List<Map<String, Object>> newestCommentsList = commentRepository.findNewestComments();
            if (!newestCommentsList.isEmpty()) {
                logger.info("Newest comments found");
                return TransferToNewestCommentVO(newestCommentsList);
            } else {
                logger.info("No newest comments found");
            }
        } catch (Exception e) {
            logger.error("Failed to get newest comments", e);
        }
        return Collections.emptyList();
    }

    private static List<NewestCommentVO> TransferToNewestCommentVO(List<Map<String, Object>> newestCommentsList) {
        logger.info("Transferring to newest comment VO");
        List<NewestCommentVO> newestCommentVOList = new ArrayList<>();
        for (Map<String, Object> map : newestCommentsList) {
            try {
                NewestCommentVO newestCommentVO = new NewestCommentVO();
                newestCommentVO.setGenreId(Byte.valueOf(String.valueOf(map.get("genre_id"))));
                newestCommentVO.setGameId(Short.valueOf(String.valueOf(map.get("game_id"))));
                newestCommentVO.setPostId(String.valueOf(map.get("post_id")));
                newestCommentVO.setContent(String.valueOf(map.get("content")));
                newestCommentVO.setGameName(String.valueOf( map.get("game_name")));
                Timestamp timestamp = (Timestamp) map.get("created_at");
                newestCommentVO.setCreatedAt(timestamp.toInstant());
                newestCommentVOList.add(newestCommentVO);
            } catch (Exception e) {
                logger.error("Failed to transfer to newest comment VO", e);
            }
        }
        return newestCommentVOList;
    }

    public Long GetCommentAuthorByCommentId(Long commentId) {
        logger.info("Getting comment author");
        try {
            PostComment postComment = commentRepository.findById(commentId).orElse(null);
            if (postComment != null) {
                Long commentAuthor = postComment.getFromUid();
                logger.info("Comment author found");
                return commentAuthor;
            } else {
                logger.info("Comment author not found");
            }
        } catch (Exception e) {
            logger.error("Failed to get comment author", e);
        }
        return null;
    }
}

