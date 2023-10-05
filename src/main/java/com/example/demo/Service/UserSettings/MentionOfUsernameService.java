package com.example.demo.Service.UserSettings;

import com.example.demo.Mapper.Repository.MentionOfUsernameRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.MentionOfUsername;
import com.example.demo.Model.Entity.UsersVerificationToken;
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
    public void SetMentionOfUsername(UsersVerificationToken usersVerificationToken) {
        logger.info("Setting MentionOfUsername");
        try{
            MentionOfUsername mentionOfUsername = new MentionOfUsername();
            mentionOfUsername.setId(usersVerificationToken.getId());
            mentionOfUsername.setMentionOn(true);
            mentionOfUsername.setCreatedAt(usersVerificationToken.getModifiedAt());
            mentionOfUsername.setModifiedAt(usersVerificationToken.getModifiedAt());
            mentionOfUsernameRepository.save(mentionOfUsername);
        } catch (Exception e) {
            logger.error("Failed to save mention of username setting: {}", e.getMessage(), e);
        }
    }
}
