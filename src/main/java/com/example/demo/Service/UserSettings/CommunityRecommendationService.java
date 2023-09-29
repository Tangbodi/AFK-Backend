package com.example.demo.Service.UserSettings;

import com.example.demo.Mapper.Repository.CommunityRecommendationRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.CommunityRecommendation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CommunityRecommendationService {
    private static final Logger logger = LoggerFactory.getLogger(CommunityRecommendationService.class);
    @Autowired
    private CommunityRecommendationRepository communityRecommendationRepository;
    @Async("MultiExecutor")
    @Transactional
    public void SaveCommunityRecommendation(UserRegisterDTO userRegisterDTO) {
        logger.info("Saving community recommendation");
        try {
            CommunityRecommendation communityRecommendation = new CommunityRecommendation();
            communityRecommendation.setId(userRegisterDTO.getUserId());
            communityRecommendation.setMentionOn(true);
            communityRecommendation.setCreatedAt(userRegisterDTO.getCreatedAt());
            communityRecommendation.setModifiedAt(userRegisterDTO.getCreatedAt());
            communityRecommendationRepository.save(communityRecommendation);
        } catch (Exception e) {
            logger.error("Failed to save community recommendation", e.getMessage(), e);
        }
    }
}
