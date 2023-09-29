package com.example.demo.Service.UserSettings;

import com.example.demo.Mapper.Repository.TrendingPostRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.TrendingPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class TrendingPostService {
    private static final Logger logger = LoggerFactory.getLogger(TrendingPostService.class);
    @Autowired
    private TrendingPostRepository trendingPostRepository;
    @Async("MultiExecutor")
    @Transactional
    public void SaveTrendingPost(UserRegisterDTO userRegisterDTO){
        logger.info("Saving trending post");
        try{
            TrendingPost trendingPost = new TrendingPost();
            trendingPost.setId(userRegisterDTO.getUserId());
            trendingPost.setMentionOn(true);
            trendingPost.setCreatedAt(userRegisterDTO.getCreatedAt());
            trendingPost.setModifiedAt(userRegisterDTO.getCreatedAt());
            trendingPostRepository.save(trendingPost);
        } catch (Exception e) {
            logger.error("Failed to save trending post", e.getMessage(), e);
        }
    }
}
