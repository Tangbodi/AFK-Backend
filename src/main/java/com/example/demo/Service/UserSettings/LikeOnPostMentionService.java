package com.example.demo.Service.UserSettings;

import com.example.demo.Mapper.Repository.LikeOnPostMentionRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.LikeOnPostMention;
import com.example.demo.Model.Entity.UsersVerificationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class LikeOnPostMentionService {
    private static final Logger logger = LoggerFactory.getLogger(LikeOnPostMentionService.class);
    @Autowired
    private LikeOnPostMentionRepository likeOnPostMentionRepository;
    @Async("MultiExecutor")
    @Transactional
    public void SetLikeOnPostMention(UsersVerificationToken usersVerificationToken) {
        logger.info("Setting LikeOnPostMention");
        try{
            LikeOnPostMention likeOnPostMention = new LikeOnPostMention();
            likeOnPostMention.setId(usersVerificationToken.getId());
            likeOnPostMention.setMentionOn(true);
            likeOnPostMention.setCreatedAt(usersVerificationToken.getModifiedAt());
            likeOnPostMention.setModifiedAt(usersVerificationToken.getModifiedAt());
            likeOnPostMentionRepository.save(likeOnPostMention);
        } catch (Exception e) {
            logger.error("Failed to save like on post mention setting: {}", e.getMessage(), e);
        }
    }
}
