package com.example.demo.Service.UserSettings;

import com.example.demo.Mapper.Repository.ReplyOnCommentMentionRepository;
import com.example.demo.Model.DTO.UserRegisterDTO;
import com.example.demo.Model.Entity.ReplyOnCommentMention;
import com.example.demo.Model.Entity.UsersVerificationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ReplyOnCommentMentionService {
    private static final Logger logger = LoggerFactory.getLogger(ReplyOnCommentMentionService.class);
    @Autowired
    private ReplyOnCommentMentionRepository replyOnCommentMentionRepository;

    @Async("MultiExecutor")
    @Transactional
    public void SaveReplyOnCommentMention(UsersVerificationToken usersVerificationToken) {
        logger.info("Saving ReplyOnCommentMention:{}");
        try {
            ReplyOnCommentMention replyOnCommentMention = new ReplyOnCommentMention();
            replyOnCommentMention.setId(usersVerificationToken.getId());
            replyOnCommentMention.setMentionOn(true);
            replyOnCommentMention.setCreatedAt(usersVerificationToken.getModifiedAt());
            replyOnCommentMention.setModifiedAt(usersVerificationToken.getModifiedAt());
            replyOnCommentMentionRepository.save(replyOnCommentMention);
            logger.info("Saved reply on comment mention setting");
        } catch (Exception e) {
            logger.error("Failed to save reply on comment mention setting: {}", e.getMessage(), e);
        }
    }
}
