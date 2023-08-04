package com.example.demo.Service.Comments;

import com.example.demo.Mapper.Repository.CommentRepository;
import com.example.demo.Model.DTO.CommentDTO;
import com.example.demo.Model.Entity.PostComment;
import com.example.demo.Model.VO.CommentVO;
import com.example.demo.Model.VO.PostVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    @Autowired
    private CommentRepository commentRepository;

    @Transactional
    public CommentVO SetComment(CommentDTO commentDTO) {
        logger.info("Setting comment: {}");
        try {
            PostComment postComment = new PostComment();
            String uuid = UUID.randomUUID().toString();
            commentDTO.setCommentId(uuid);
            postComment.setId(uuid);
            postComment.setPostId(commentDTO.getPostId());
            postComment.setContent(commentDTO.getContent());
            postComment.setFromUid(commentDTO.getFromUid());
            postComment.setIpvFour(commentDTO.getIpvFour());
            postComment.setIpvSix(commentDTO.getIpvSix());
            postComment.setCreatedAt(commentDTO.getCreatedAt());
            postComment.setModifiedAt(commentDTO.getCreatedAt());
            if(commentRepository.save(postComment) != null) {
                logger.info("Comment saved successfully: {}");
                CommentVO commentVO = TransferToVO(commentDTO);
                return commentVO;
            } else {
                //do nothing
            }
        } catch (Exception e) {
            logger.error("Failed to set comment: {}", e.getMessage(), e);
        }
        return null;
    }
    public CommentVO TransferToVO(CommentDTO commentDTO) {
        CommentVO commentVO = new CommentVO();
        commentVO.setCommentId(commentDTO.getCommentId());
        commentVO.setPostId(commentDTO.getPostId());
        commentVO.setCreatedAt(commentDTO.getCreatedAt());
        return commentVO;
    }
    public List<Map<Short, Object>> GetAllCommentsByPostId(String postId){
        return commentRepository.findByPostId(postId);
    }
}
