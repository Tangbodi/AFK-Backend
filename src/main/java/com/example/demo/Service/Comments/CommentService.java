package com.example.demo.Service.Comments;

import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.Entity.PostComment;
import com.example.demo.Model.VO.*;
import com.example.demo.Mapper.Repository.CommentRepository;
import com.example.demo.Service.IP.IpAddressService;
import com.example.demo.Service.MQ.MQSender;
import com.example.demo.Service.Message.MessageService;
import com.example.demo.Service.Posts.PostInfoService;
import com.example.demo.Service.Replies.ReplyService;
import com.example.demo.Util.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private static final String COMMENT = "comment";
    private static final String REPLY = "reply";
    @Autowired
    private CommentRepository commentRepository;
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
    @Transactional
    public CommentSavedVO SetComment(CommentReplyDTO commentReplyDTO) {
        logger.info("Setting comment: {}");
        try {
            long commentId = Snowflake.generateUniqueId();
            commentReplyDTO.setCommentId(commentId);
            commentReplyDTO.setCreatedAt(Instant.now());
            PostComment postComment = new PostComment();
            postComment.setId(commentId);
            postComment.setContent(commentReplyDTO.getContent());
            postComment.setFromUid(commentReplyDTO.getFromUid());
            postComment.setPostId(commentReplyDTO.getPostId());
            postComment.setCreatedAt(commentReplyDTO.getCreatedAt());
            postComment.setModifiedAt(commentReplyDTO.getCreatedAt());
            PostComment savedComment = commentRepository.save(postComment);
            if (savedComment != null) {
                logger.info("Comment saved successfully: {}", savedComment);
                //set comment reply ip address
                ipAddressService.SetCommentIpAddress(commentReplyDTO);
                //send comment count message to ActiveMQ
                mqSender.SendCommentCountMessage(commentReplyDTO);
                //Set mention message after saved comment if the user is not the author of the post
                if(!commentReplyDTO.getFromUid().equals(commentReplyDTO.getToUid())){
                    logger.info("FromUid is not equal to ToUid");
                    //set message mention
                    MessageVO messageVO = new MessageVO();
                    messageVO.setCommentReplyId(commentReplyDTO.getCommentId().toString());
                    messageVO.setContent(commentReplyDTO.getContent());
                    messageVO.setFromUid(commentReplyDTO.getFromUid().toString());
                    messageVO.setFromUsername(commentReplyDTO.getFromUsername());
                    messageVO.setToUid(commentReplyDTO.getToUid().toString());
                    messageVO.setCreatedAt(commentReplyDTO.getCreatedAt().toString());
                    //set message
                    messageService.SetMessage(commentReplyDTO);
                    //send message mention to MQ
                    mqSender.SendMentionMessage(messageVO);
                } else {
                    logger.info("FromUid is equal to ToUid");
                }
                return TransferToVO(commentReplyDTO);
            } else {
                logger.info("Comment not saved: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to set comment: {}", e.getMessage(), e);
        }
        return null;
    }

    private static CommentSavedVO TransferToVO(CommentReplyDTO commentReplyDTO) {
        CommentSavedVO commentSavedVO = new CommentSavedVO();
        commentSavedVO.setCommentId(commentReplyDTO.getCommentId().toString());
        commentSavedVO.setPostId(commentReplyDTO.getPostId().toString());
        commentSavedVO.setCreatedAt(Instant.now());
        return commentSavedVO;
    }

    public List<Map<String, Object>> GetAllCommentsByPostId(Long postId, Long userId) {
        logger.info("Getting all comments by post id: {}", postId);
        return commentRepository.findCommentsByPostId(postId, userId);
    }

    public List<Map<String, Object>> GetAllCommentsAndReplies(Long postId, Long userId) {
        logger.info("Getting all comments and replies");
        //get all comments by post id
        List<Map<String, Object>> commentsList = GetAllCommentsByPostId(postId, userId);
        List<Map<String, Object>> res = new ArrayList<>();
        //get all comment ids from all comments for getting all replies with same comment ids
        if (commentsList.isEmpty()){
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
            if(repliesList.isEmpty()){
                logger.info("No replies found");
                for(Map<String, Object> comment : commentsList){
                    ShowCommentVO showCommentVO = CreateCommentMap(comment);
                    List<Map<String, Object>> combinedList = new ArrayList<>();
                    Map<String,Object> map = new HashMap<>();
                    map.put(COMMENT,showCommentVO);
                    map.put(REPLY,Collections.emptyList());
                    res.add(map);
                }
            } else {
                logger.info("Replies found");
                //final result list
                //traverse all comments and pick out replies with same comment id
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
                    Map<String,Object> map = new HashMap<>();
                    map.put(COMMENT,showCommentVO);
                    map.put(REPLY,replies);
                    res.add(map);
                }
            }
            return res;
        }
    }
    private static ShowCommentVO CreateCommentMap(Map<String, Object> comment){
        logger.info("Creating comment map");
//        pc.comment_id, p.post_id, pc.from_uid, ui.username, ui.avatar_url, pc.content, ulc.like_status, pc.created_at
        ShowCommentVO showCommentVO = new ShowCommentVO();
        showCommentVO.setCommentId(comment.get("comment_id").toString());
        showCommentVO.setPostId(comment.get("post_id").toString());
        showCommentVO.setFromUid(comment.get("from_uid").toString());
        showCommentVO.setUsername(comment.get("username").toString());
        showCommentVO.setFromAvatarURL(comment.get("fm_avatar_url").toString());
        showCommentVO.setContent(comment.get("content").toString());
        showCommentVO.setLikeStatus((Integer) comment.get("like_status"));
        Timestamp timestamp = (Timestamp) comment.get("created_at");
        showCommentVO.setCreatedAt(timestamp.toInstant());

        return showCommentVO;
    }
    private static ShowReplyVO CreateReplyMap(Map<String, Object> reply){
        logger.info("Creating reply map");

        ShowReplyVO showReplyVO = new ShowReplyVO();
        showReplyVO.setReplyId(reply.get("reply_id").toString());
        showReplyVO.setCommentId(reply.get("comment_id").toString());
        showReplyVO.setToReplyId(reply.get("to_reply_id").toString());
        showReplyVO.setFromUid(reply.get("from_uid").toString());
        showReplyVO.setFromAvatarURL(reply.get("fm_avatar_url").toString());
        showReplyVO.setFromUsername(reply.get("fm_username").toString());
        showReplyVO.setToUid(reply.get("to_uid").toString());
        showReplyVO.setToUsername(reply.get("to_username").toString());
        showReplyVO.setContent(reply.get("content").toString());
        showReplyVO.setLikeStatus((Integer)reply.get("like_status"));
        Timestamp timestamp = (Timestamp) reply.get("created_at");
        showReplyVO.setCreatedAt(timestamp.toInstant());

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
                newestCommentVO.setPostId(map.get("post_id").toString());
                newestCommentVO.setContent((String) map.get("content"));
                newestCommentVO.setGameName((String) map.get("game_name"));
                Timestamp timestamp = (Timestamp) map.get("created_at");
                newestCommentVO.setCreatedAt(timestamp.toInstant());
                newestCommentVOList.add(newestCommentVO);
            } catch (Exception e) {
                logger.error("Failed to transfer to newest comment VO", e);
            }
        }
        return newestCommentVOList;
    }
}

