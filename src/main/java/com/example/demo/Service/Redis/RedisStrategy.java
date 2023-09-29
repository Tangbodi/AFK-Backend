package com.example.demo.Service.Redis;

import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Mapper.Repository.CommentRepository;
import com.example.demo.Mapper.Repository.PostRepository;
import com.example.demo.Mapper.Repository.ReplyRepository;
import com.example.demo.Model.DTO.*;
import com.example.demo.Service.UserSettings.CommentOnPostMentionService;
import com.example.demo.Service.EmailValidation.ProcessEmailService;
import com.example.demo.Service.Message.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RedisStrategy {
    private static final Logger logger = LoggerFactory.getLogger(RedisStrategy.class);
    private static final String SAVED_GAME = ObjectNameEnum.SAVED_GAME_SET.getTypeName();
    @Autowired
    private RedisService redisService;
    @Autowired
    private ProcessEmailService processEmailService;
    @Autowired
    private RedisGameIconService redisGameIconService;
    @Autowired
    private RedisUserFavoriteGameService redisUserFavoriteGameService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MessageService messageService;
    @Autowired
    private CommentOnPostMentionService commentOnPostMentionService;
    @Autowired
    private RedisUserSettingService redisUserSettingService;
    @Autowired
    private RedisUserLikeSaveService redisUserLikeSaveService;
    @Autowired
    private RedisReplyService redisReplyService;
    @Autowired
    private RedisCommentService redisCommentService;

    public void UserRegistrationStrategy(UserRegisterDTO userRegisterDTO) {
        logger.info("Start UserRegistrationStrategy");
        processEmailService.ProcessRegistrationEmailValidation(userRegisterDTO);
    }

    public void UserFavoriteGameStrategy(UserLikeSaveDTO userLikeSaveDTO) throws IOException {
        logger.info("Start UserFavoriteGameStrategy");
        redisUserFavoriteGameService.HandleUserFavoriteGameStrategy(userLikeSaveDTO);
    }

    public void LikeSaveStrategy(UserLikeSaveDTO userLikeSaveDTO) throws InterruptedException {
        logger.info("Start LikeSaveStrategy");
        redisUserLikeSaveService.HandleLikeSaveStrategy(userLikeSaveDTO);
    }

    public void LikeSaveMentionStrategy(UserLikeSaveDTO userLikeSaveDTO) {
        logger.info("Start LikeSaveMentionStrategy");
        redisUserLikeSaveService.HandleLikeSaveMentionStrategy(userLikeSaveDTO);
    }

    public void CommentCountStrategy(CommentReplyDTO commentReplyDTO) {
        logger.info("Start CommentCountStrategy");
        redisCommentService.HandleCommentCountStrategy(commentReplyDTO);
    }

    public void ReplyCountStrategy(CommentReplyDTO commentReplyDTO) {
        logger.info("Start ReplyCountStrategy");
        redisReplyService.HandleReplyCountStrategy(commentReplyDTO);
    }

    public void MessageMentionStrategy(Long userId) {
        logger.info("Start MessageMentionStrategy");
        messageService.GetUnreadMessageByUserId(userId);
    }

    public void UpdateEmailStrategy(EmailDTO emailDTO) {
        logger.info("Start UpdateEmailStrategy");
        processEmailService.ProcessUpdateEmailValidation(emailDTO);
    }

    public void ForgotPasswordStrategy(EmailDTO emailDTO) {
        logger.info("Start ForgotPasswordStrategy");
        processEmailService.ProcessForgotPasswordEmailValidation(emailDTO);
    }
    public void UserSettingStrategy(UserSettingDTO userSettingDTO) throws IOException {
        logger.info("Start UserSettingStrategy");
        redisUserSettingService.HandleUserSettingStrategy(userSettingDTO);
    }
}
