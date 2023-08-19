package com.example.demo.Service.Comments;

import com.example.demo.Mapper.Repository.CommentRepository;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.DTO.IpAddressDTO;
import com.example.demo.Model.Entity.PostComment;
import com.example.demo.Model.VO.CommentVO;
import com.example.demo.Model.VO.NewestCommentVO;
import com.example.demo.Service.IP.IpAddressService;
import com.example.demo.Service.IP.IpService;
import com.example.demo.Service.Replies.ReplyService;
import com.example.demo.Util.UUIDCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
            String uuid = UUIDCreator.CreateUUID();
            commentReplyDTO.setCommentId(uuid);
            commentReplyDTO.setCreatedAt(Instant.now());
            PostComment postComment = new PostComment();
            postComment.setId(uuid);
            postComment.setContent(commentReplyDTO.getContent());
            postComment.setFromUid(commentReplyDTO.getFromUid());
            postComment.setPostId(commentReplyDTO.getPostId());
            postComment.setCreatedAt(commentReplyDTO.getCreatedAt());
            postComment.setModifiedAt(commentReplyDTO.getCreatedAt());
            PostComment savedComment = commentRepository.save(postComment);
            if (savedComment != null) {
                logger.info("Comment saved successfully: {}", savedComment);
                IpAddressDTO ipAddressDTO = new IpAddressDTO();
                ipAddressDTO.setId(commentReplyDTO.getCommentId());
                ipAddressDTO.setIpvFour(commentReplyDTO.getIpvFour());
                ipAddressDTO.setIpvSix(commentReplyDTO.getIpvSix());
                ipAddressDTO.setCreatedAt(commentReplyDTO.getCreatedAt());
                ipAddressService.SetIpAddress(ipAddressDTO);
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

    public List<Map<Short, Object>> GetAllCommentsByPostId(String postId) {
        return commentRepository.findCommentsByPostId(postId);
    }
    public List<List<Object>> GetAllCommentsAndReplies(String postId){
        List<Map<Short, Object>> commentsList = GetAllCommentsByPostId(postId);
        List<String> commentIds = new ArrayList<>();
        for (Map<Short, Object> row : commentsList) {
            commentIds.add((String) row.get("comment_id"));
        }
        List<Map<Short, Object>> replyList = replyService.GetRepliesByCommentId(commentIds);
        List<List<Object>> res = new ArrayList<>();
        for (Map<Short, Object> commentRow : commentsList) {
            String commentId = (String) commentRow.get("comment_id");
            List<Map<String, String>> replies = new ArrayList<>();
            for (Map<Short, Object> replyRow : replyList) {
                Map<String, String> repliesMap = new HashMap<>();
                String commentId2 = (String) replyRow.get("comment_id");
                if (commentId.equals(commentId2)) {
                    repliesMap.put("reply_id", (String) replyRow.get("reply_id"));
                    repliesMap.put("comment_id", (String) replyRow.get("comment_id"));
                    repliesMap.put("from_uid", (String) replyRow.get("from_uid"));
                    repliesMap.put("fmui_avatar_url", (String) replyRow.get("fmui_avatar_url"));
                    repliesMap.put("from_username", (String) replyRow.get("from_username"));
                    repliesMap.put("to_uid", (String) replyRow.get("to_uid"));
                    repliesMap.put("to_username", (String) replyRow.get("to_username"));
                    repliesMap.put("content", (String) replyRow.get("content"));
                    repliesMap.put("created_at", replyRow.get("created_at").toString());
                    replies.add(repliesMap);
                } else {
                    continue;
                }
            }
            List<Object> combinedList = new ArrayList<>();
            combinedList.add(commentRow);
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
                newestCommentVO.setPostId((String) map.get("post_id"));
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

