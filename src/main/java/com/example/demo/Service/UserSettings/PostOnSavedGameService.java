package com.example.demo.Service.UserSettings;

import com.example.demo.Mapper.Repository.PostOnSavedGameMentionRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.PostOnSavedGameMention;
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
    public void SetPostOnSavedGame(UserRegisterDTO userRegisterDTO) {
        logger.info("Saving post on saved game");
        try {
            PostOnSavedGameMention postOnSavedGameMention = new PostOnSavedGameMention();
            postOnSavedGameMention.setId(userRegisterDTO.getUserId());
            postOnSavedGameMention.setMentionOn(true);
            postOnSavedGameMention.setCreatedAt(userRegisterDTO.getCreatedAt());
            postOnSavedGameMention.setModifiedAt(userRegisterDTO.getCreatedAt());
            postOnSavedGameMentionRepository.save(postOnSavedGameMention);
        } catch (Exception e) {
            logger.error("Failed to save post on saved game", e.getMessage(), e);
        }
    }
}
