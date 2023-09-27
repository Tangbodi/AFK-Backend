package com.example.demo.Service.UserLikeSave;

import com.example.demo.Mapper.Repository.MentionOfUsernameRepository;
import com.example.demo.Mapper.Repository.SaveOnPostMentionRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.MentionOfUsername;
import com.example.demo.Model.Entity.SaveOnPostMention;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class MentionOfUsernameService {
    private static final Logger logger = LoggerFactory.getLogger(MentionOfUsernameService.class);
    @Autowired
    private MentionOfUsernameRepository mentionOfUsernameRepository;
    @Async("MultiExecutor")
    @Transactional
    public void SetSaveOnPostMention(UserRegisterDTO userRegisterDTO) {
        logger.info("Setting MentionOfUsername");
        try{
            MentionOfUsername mentionOfUsername = new MentionOfUsername();
            mentionOfUsername.setId(userRegisterDTO.getUserId());
            mentionOfUsername.setMentionOn(true);
            mentionOfUsername.setCreatedAt(userRegisterDTO.getCreatedAt());
            mentionOfUsername.setModifiedAt(userRegisterDTO.getCreatedAt());
            mentionOfUsernameRepository.save(mentionOfUsername);
        } catch (Exception e) {
            logger.error("Failed to save mention of username setting: {}", e.getMessage(), e);
        }
    }
}
