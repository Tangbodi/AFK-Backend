package com.example.demo.Service.UserSettings;

import com.example.demo.Mapper.Repository.PostOnSavedGameMentionRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.PostOnSavedGameMention;
import com.example.demo.Model.Entity.UsersVerificationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class PostOnSavedGameService {
    private static final Logger logger = LoggerFactory.getLogger(PostOnSavedGameService.class);

    @Autowired
    private PostOnSavedGameMentionRepository postOnSavedGameMentionRepository;
    @Async("MultiExecutor")
    @Transactional
    public void SetPostOnSavedGame(UsersVerificationToken usersVerificationToken) {
        logger.info("Saving post on saved game");
        try {
            PostOnSavedGameMention postOnSavedGameMention = new PostOnSavedGameMention();
            postOnSavedGameMention.setId(usersVerificationToken.getId());
            postOnSavedGameMention.setMentionOn(true);
            postOnSavedGameMention.setCreatedAt(usersVerificationToken.getModifiedAt());
            postOnSavedGameMention.setModifiedAt(usersVerificationToken.getModifiedAt());
            postOnSavedGameMentionRepository.save(postOnSavedGameMention);
        } catch (Exception e) {
            logger.error("Failed to save post on saved game", e.getMessage(), e);
        }
    }
}
