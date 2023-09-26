package com.example.demo.Service.Comments;

import com.example.demo.Model.DTO.ObjectUserDTO;
import com.example.demo.Model.Entity.CommentsInfo;
import com.example.demo.Model.Entity.UsersLikeComment;
import com.example.demo.Mapper.Repository.CommentInfoRepository;
import com.example.demo.Mapper.Repository.UserLikeCommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class CommentInfoService {
    private static final Logger logger = LoggerFactory.getLogger(CommentInfoService.class);
    @Autowired
    private CommentInfoRepository commentInfoRepository;
    @Autowired
    private UserLikeCommentRepository userLikeCommentRepository;

    public void CalculateCommentTotalLike( List<Long> commentIds){
        logger.info("Finding all users like comments list with like status = 1");
        for(Long commentId : commentIds){
            Map<String,Object> map = userLikeCommentRepository.findCommentTotalLikeByLikeStatus(commentId);
            Integer totalLike= ((BigInteger) map.get("total_like")).intValue();
            UpdateCommentLikeCount(commentId, totalLike);
        }
    }
    public void UpdateCommentLikeCount(Long commentId, Integer totalLike){
        logger.info("Updating comment like count");
        CommentsInfo commentsInfo = commentInfoRepository.findById(commentId)
                .orElseGet(() -> CreateCommentInfo(commentId));
        commentsInfo.setLike(totalLike);
        commentInfoRepository.save(commentsInfo);
        logger.info("Updated comment like count");
    }
    private static CommentsInfo CreateCommentInfo(Long commentId){
        logger.info("Creating comment info");
        CommentsInfo commentsInfo = new CommentsInfo();
        commentsInfo.setId(commentId);
        commentsInfo.setLike(0);
        logger.info("Created comment info for comment ID: {}", commentId);
        return commentsInfo;
    }
}
