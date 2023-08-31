package com.example.demo.Service.Comments;

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
    public void CalculateCommentTotalLike(){
        logger.info("Finding all users like comments list with like status = 1");
        List<UsersLikeComment> likeList = userLikeCommentRepository.findAllByLikeStatus();
        if (likeList.isEmpty()){
            logger.info("No users like comments found");
        } else {
            logger.info("Found users like comments list with like status = 1");
            logger.info("Traverse users like comments list");
            for (UsersLikeComment usersLikeComment : likeList) {
                Long commentId = usersLikeComment.getId().getCommentId();
                logger.info("Comment ID: {}", commentId);
                Map<String,Object> map = userLikeCommentRepository.findTotalLike(commentId);
                Integer totalLike =  ((BigInteger) map.get("total_like")).intValue();
                UpdateCommentLikeCount(commentId, totalLike);
            }
        }
    }
}
