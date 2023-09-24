package com.example.demo.Service.Comments;

import com.example.demo.Mapper.Repository.CommentOnPostMentionRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.CommentOnPostMention;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CommentOnPostMentionService {
    private static final Logger logger = LoggerFactory.getLogger(CommentOnPostMentionService.class);
    @Autowired
    private CommentOnPostMentionRepository commentOnPostMentionRepository;

    @Async("MultiExecutor")
    @Transactional
    public void SaveCommentOnPostMention(UserRegisterDTO userRegisterDTO){
        logger.info("Saving CommentOnPostMention:{}");
        try{
            CommentOnPostMention commentOnPostMention = new CommentOnPostMention();
            commentOnPostMention.setId(userRegisterDTO.getUserId());
            commentOnPostMention.setMentionOn(true);
            commentOnPostMention.setCreatedAt(userRegisterDTO.getCreatedAt());
            commentOnPostMention.setModifiedAt(userRegisterDTO.getCreatedAt());
            commentOnPostMentionRepository.save(commentOnPostMention);
            logger.info("Saved comment on post mention setting");
        }catch (Exception e){
            logger.error("Failed to save comment on post mention setting: {}",e.getMessage(),e);
        }
    }
    public boolean CheckCommentOnPostMention(Long userId){
        logger.info("Checking comment on post mention setting for user:{}",userId);
        try{
            CommentOnPostMention commentOnPostMention = commentOnPostMentionRepository.findById(userId).orElse(null);
            if(commentOnPostMention == null){
                logger.info("User not found");
                return false;
            } else {
                logger.info("User found");
                logger.info("Comment on post mention setting is:{}",commentOnPostMention.getMentionOn());
                return commentOnPostMention.getMentionOn();
            }
        }catch (Exception e){
            logger.error("Failed to check comment on post mention setting: {}",e.getMessage(),e);
            return false;
        }
    }
}
