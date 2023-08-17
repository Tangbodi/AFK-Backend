package com.example.demo.Service.Comments;

import com.example.demo.Mapper.Repository.CommentRepository;
import com.example.demo.Model.DTO.CommentDTO;
import com.example.demo.Model.Entity.PostComment;
import com.example.demo.Model.VO.CommentVO;
import com.example.demo.Service.Replies.ReplyService;
import com.example.demo.Util.UUIDCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReplyService replyService;
    @Transactional
    public CommentVO SetComment(CommentDTO commentDTO) {
        logger.info("Setting comment: {}", commentDTO);
        try {
            PostComment postComment = new PostComment();
            String uuid = UUIDCreator.CreateUUID();
            commentDTO.setCommentId(uuid);
            postComment.setId(uuid);
            MapCommentDTOtoPostComment(commentDTO, postComment);

            PostComment savedComment = commentRepository.save(postComment);
            if (savedComment != null) {
                logger.info("Comment saved successfully: {}", savedComment);
                return TransferToVO(commentDTO);
            } else {
                logger.warn("Comment not saved: {}", commentDTO);
            }
        } catch (Exception e) {
            logger.error("Failed to set comment: {}", e.getMessage(), e);
        }
        return null;
    }

    private static CommentVO TransferToVO(CommentDTO commentDTO) {
        CommentVO commentVO = new CommentVO();
        commentVO.setCommentId(commentDTO.getCommentId());
        commentVO.setPostId(commentDTO.getPostId());
        commentVO.setCreatedAt(commentDTO.getCreatedAt());
        return commentVO;
    }

    public List<Map<Short, Object>> GetAllCommentsByPostId(String postId) {
        return commentRepository.findByPostId(postId);
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
                    repliesMap.put("from_username", (String) replyRow.get("from_username"));
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
    private static void MapCommentDTOtoPostComment(CommentDTO commentDTO, PostComment postComment) {
        postComment.setPostId(commentDTO.getPostId());
        postComment.setContent(commentDTO.getContent());
        postComment.setFromUid(commentDTO.getFromUid());
        postComment.setIpvFour(commentDTO.getIpvFour());
        postComment.setIpvSix(commentDTO.getIpvSix());
        postComment.setCreatedAt(commentDTO.getCreatedAt());
        postComment.setModifiedAt(commentDTO.getCreatedAt());
    }
}

