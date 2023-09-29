package com.example.demo.Service.UserSettings;

import com.example.demo.Mapper.Repository.LikeOnCommentMentionRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.LikeOnCommentMention;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class LikeOnCommentMentionService {
    private static final Logger logger = LoggerFactory.getLogger(LikeOnCommentMentionService.class);
    @Autowired
    private LikeOnCommentMentionRepository likeOnCommentMentionRepository;
    @Async("MultiExecutor")
    @Transactional
    public void SetLikeOnCommentMention(UserRegisterDTO userRegisterDTO) {
        logger.info("Setting LikeOnCommentMention");
        try {
            LikeOnCommentMention likeOnCommentMention = new LikeOnCommentMention();
            likeOnCommentMention.setId(userRegisterDTO.getUserId());
            likeOnCommentMention.setMentionOn(true);
            likeOnCommentMention.setCreatedAt(userRegisterDTO.getCreatedAt());
            likeOnCommentMention.setModifiedAt(userRegisterDTO.getCreatedAt());
            likeOnCommentMentionRepository.save(likeOnCommentMention);
        } catch (Exception e) {
            logger.error("Failed to save like on comment mention setting: {}", e.getMessage(), e);
        }
    }
}
