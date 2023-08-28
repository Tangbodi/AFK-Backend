package com.example.demo.Service.Comments;

import com.example.demo.Model.Entity.CommentsInfo;
import com.example.demo.Repository.CommentInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CommentInfoService {
    private static final Logger logger = LoggerFactory.getLogger(CommentInfoService.class);
    @Autowired
    private CommentInfoRepository commentInfoRepository;

    public void UpdateCommentLikeCount(Long commentId){
        logger.info("Updating comment like count");
        CommentsInfo commentsInfo = commentInfoRepository.findById(commentId)
                .orElseGet(() -> CreateCommentInfo(commentId));
        commentsInfo.setLike(commentsInfo.getLike()+1);
        commentInfoRepository.save(commentsInfo);
        logger.info("Updated comment like count");
    }
    public CommentsInfo CreateCommentInfo(Long commentId){
        logger.info("Creating comment info");
        CommentsInfo commentsInfo = new CommentsInfo();
        commentsInfo.setId(commentId);
        commentsInfo.setLike(0);
        logger.info("Created comment info for comment ID: {}", commentId);
        return commentsInfo;
    }
}
