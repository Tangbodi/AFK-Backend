package com.example.demo.Service.UserSettings;

import com.example.demo.Mapper.Repository.SaveOnPostMentionRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.SaveOnPostMention;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class SaveOnPostMentionService {
    private static final Logger logger = LoggerFactory.getLogger(SaveOnPostMentionService.class);
    @Autowired
    private SaveOnPostMentionRepository saveOnPostMentionRepository;
    @Async("MultiExecutor")
    @Transactional
    public void SetSaveOnPostMention(UserRegisterDTO userRegisterDTO) {
        logger.info("Setting SaveOnPostMention");
        try{
            SaveOnPostMention saveOnPostMention = new SaveOnPostMention();
            saveOnPostMention.setId(userRegisterDTO.getUserId());
            saveOnPostMention.setMentionOn(true);
            saveOnPostMention.setCreatedAt(userRegisterDTO.getCreatedAt());
            saveOnPostMention.setModifiedAt(userRegisterDTO.getCreatedAt());
            saveOnPostMentionRepository.save(saveOnPostMention);
        } catch (Exception e) {
            logger.error("Failed to save save on post mention setting: {}", e.getMessage(), e);
        }
    }
}
