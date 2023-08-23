package com.example.demo.Service.Comments;

import com.example.demo.Mapper.Repository.CommentRepository;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.Entity.PostComment;
import com.example.demo.Model.VO.CommentVO;
import com.example.demo.Model.VO.NewestCommentVO;
import com.example.demo.Service.IP.IpAddressService;
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
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ReplyService replyService;
    @Autowired
    private IpAddressService ipAddressService;

    @Transactional
    public CommentVO SetComment(CommentReplyDTO commentReplyDTO) {
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
                ipAddressService.SetCommentReplyIpAddress(commentReplyDTO);
                return TransferToVO(commentReplyDTO);
            } else {
                logger.info("Comment not saved: {}");
            }
        } catch (Exception e) {
            logger.error("Failed to set comment: {}", e.getMessage(), e);
        }
        return null;
    }

    private static CommentVO TransferToVO(CommentReplyDTO commentReplyDTO) {
        CommentVO commentVO = new CommentVO();
        commentVO.setCommentId(commentReplyDTO.getCommentId());
        commentVO.setPostId(commentReplyDTO.getPostId());
        commentVO.setCreatedAt(Instant.now());
        return commentVO;
    }

    public List<Map<Short, Object>> GetAllCommentsByPostId(Long postId) {
        logger.info("Getting all comments by post id: {}", postId);
        return commentRepository.findCommentsByPostId(postId);
    }

    public List<List<Object>> GetAllCommentsAndReplies(Long postId) {
        logger.info("Getting all comments and replies");
        List<Map<Short, Object>> commentsList = GetAllCommentsByPostId(postId);
        List<Long> commentIds = new ArrayList<>();
        for (Map<Short, Object> comment : commentsList) {
            commentIds.add(((BigInteger) comment.get("comment_id")).longValue());
        }
        List<Map<Short, Object>> repliesList = replyService.GetRepliesByCommentId(commentIds);
        List<List<Object>> res = new ArrayList<>();
        for (Map<Short, Object> comment : commentsList) {
            Long commentId = ((BigInteger) comment.get("comment_id")).longValue();
            List<Map<String, Object>> replies = new ArrayList<>();
            for (Map<Short, Object> reply : repliesList) {
                Map<String, Object> repliesMap = new HashMap<>();
                Long commentId2 = ((BigInteger) reply.get("comment_id")).longValue();
                if (commentId.equals(commentId2)) {
                    repliesMap.put("reply_id", reply.get("reply_id"));
                    repliesMap.put("comment_id", reply.get("comment_id"));
                    repliesMap.put("from_uid", reply.get("from_uid"));
                    repliesMap.put("fmui_avatar_url", reply.get("fmui_avatar_url"));
                    repliesMap.put("from_username", reply.get("from_username"));
                    repliesMap.put("to_uid", reply.get("to_uid"));
                    repliesMap.put("to_username", reply.get("to_username"));
                    repliesMap.put("content", reply.get("content"));
                    repliesMap.put("created_at", reply.get("created_at").toString());
                    replies.add(repliesMap);
                } else {
                    continue;
                }
                //          int startIdx = (int) pageable.getOffset();
                //                int endIdx = Math.min((startIdx + pageable.getPageSize()), replies.size());
                //                List<Map<String, Object>> currentReplyItems = replies.subList(startIdx, endIdx);
                //                Page<Map<String, Object>> currentRepliesPage = new PageImpl<>(currentReplyItems, pageable, replies.size());
            }
            List<Object> combinedList = new ArrayList<>();
            combinedList.add(comment);
            combinedList.add(replies);
            res.add(combinedList);
        }
        return res;
    }

    public List<NewestCommentVO> GetNewestComments() {
        logger.info("Getting newest comments");
        try {
            List<Map<Short, Object>> newestCommentsList = commentRepository.findNewestComments();
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

    private static List<NewestCommentVO> TransferToNewestCommentVO(List<Map<Short, Object>> newestCommentsList) {
        logger.info("Transferring to newest comment VO");
        List<NewestCommentVO> newestCommentVOList = new ArrayList<>();
        for (Map<Short, Object> map : newestCommentsList) {
            try {
                NewestCommentVO newestCommentVO = new NewestCommentVO();
                newestCommentVO.setPostId((Long) map.get("post_id"));
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

