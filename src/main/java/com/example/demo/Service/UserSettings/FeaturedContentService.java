package com.example.demo.Service.UserSettings;

import com.example.demo.Mapper.Repository.FeaturedContentRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.FeaturedContent;
import com.example.demo.Model.Entity.UsersVerificationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class FeaturedContentService {
    private static final Logger logger = LoggerFactory.getLogger(FeaturedContentService.class);
    @Autowired
    private FeaturedContentRepository featuredContentRepository;
    @Async("MultiExecutor")
    @Transactional
    public void SaveFeaturedContent(UsersVerificationToken usersVerificationToken) {
        logger.info("Saving featured content");
        try {
            FeaturedContent featuredContent = new FeaturedContent();
            featuredContent.setId(usersVerificationToken.getId());
            featuredContent.setMentionOn(true);
            featuredContent.setCreatedAt(usersVerificationToken.getModifiedAt());
            featuredContent.setModifiedAt(usersVerificationToken.getModifiedAt());
            featuredContentRepository.save(featuredContent);
        } catch (Exception e) {
            logger.error("Failed to save featured content", e.getMessage(), e);
        }
    }
}
