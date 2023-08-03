package com.example.demo.Service.Comments;

import com.example.demo.Model.DTO.CommentDTO;
import com.example.demo.Model.Entity.PostComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Transactional
    public PostComment SetComment(CommentDTO commentDTO) {
        logger.info("Setting comment: {}");
        PostComment postComment = new PostComment();
        return postComment;
    }
}
