package com.example.demo.Service.MQ;

import com.example.demo.Model.DTO.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Queue;
import java.time.Instant;

@Component
public class MQSender {
    private static final Logger logger = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    private Queue LikeSaveQueue;
    @Autowired
    private Queue CommentCountQueue;
    @Autowired
    private Queue MessageMentionQueue;
    @Autowired
    private Queue ReplyCountQueue;
    @Autowired
    private Queue UserRegistrationQueue;
    @Autowired
    private Queue UpdateEmailQueue;
    @Autowired
    private Queue ForgotPasswordQueue;
    @Autowired
    private Queue UserSettingQueue;
    @Autowired
    private Queue NewPost;

    @Async("MultiExecutor")
    public void SendUserRegistrationMessage(UserRegisterDTO userRegisterDTO) throws JMSException, InterruptedException {
        String queueName = UserRegistrationQueue.getQueueName();
        jmsMessagingTemplate.convertAndSend(queueName, userRegisterDTO);
        logger.info("Message sent, User: " + userRegisterDTO.getUsername());
    }

    @Async("MultiExecutor")
    public void SendLikeSaveMessage(UserLikeSaveDTO userLikeSaveDTO, Long userId) throws JMSException, InterruptedException {
        String queueName = LikeSaveQueue.getQueueName();
        userLikeSaveDTO.setUserId(userId);
        userLikeSaveDTO.setCreatedAt(Instant.now());
        jmsMessagingTemplate.convertAndSend(queueName, userLikeSaveDTO);
        logger.info("Message sent, User: " + userLikeSaveDTO.getUserId() + "," +
                "status: " + userLikeSaveDTO.getStatus() + "，" +
                "objectId: " + userLikeSaveDTO.getObjectId());
    }

    @Async("MultiExecutor")
    public void SendCommentCountMessage(CommentReplyDTO commentReplyDTO) throws JMSException, InterruptedException {
        String queueName = CommentCountQueue.getQueueName();
        jmsMessagingTemplate.convertAndSend(queueName, commentReplyDTO);
        logger.info("Message sent, User: " + commentReplyDTO.getFromUid() + "," +
                "commentId: " + commentReplyDTO.getCommentId() + "," +
                "postId: " + commentReplyDTO.getPostId());
    }

    @Async("MultiExecutor")
    public void SendReplyCountMessage(CommentReplyDTO commentReplyDTO) throws JMSException, InterruptedException {
        String queueName = ReplyCountQueue.getQueueName();
        jmsMessagingTemplate.convertAndSend(queueName, commentReplyDTO);
        logger.info("Message sent, User: " + commentReplyDTO.getFromUid() + "," +
                "replyId: " + commentReplyDTO.getReplyId() + "," +
                "postId: " + commentReplyDTO.getPostId());
    }

    @Async("MultiExecutor")
    public void SendMentionMessage(Long userId) throws JMSException {
        String queueName = MessageMentionQueue.getQueueName();
        jmsMessagingTemplate.convertAndSend(queueName, userId);
        logger.info("Message sent, User: " + userId);
    }

    @Async("MultiExecutor")
    public void SendUserUpdateEmailMessage(EmailDTO emailDTO) throws JMSException {
        String queueName = UpdateEmailQueue.getQueueName();
        jmsMessagingTemplate.convertAndSend(queueName, emailDTO);
        logger.info("Message sent, User: " + emailDTO.getUserId());
    }

    @Async("MultiExecutor")
    public void SendForgotPasswordMessage(EmailDTO emailDTO) throws JMSException {
        String queueName = ForgotPasswordQueue.getQueueName();
        jmsMessagingTemplate.convertAndSend(queueName, emailDTO);
        logger.info("Message sent, User: " + emailDTO.getUserId());
    }
    @Async("MultiExecutor")
    public void SendUserSettingMessage(UserSettingDTO userSettingDTO) throws JMSException {
        String queueName = UserSettingQueue.getQueueName();
        jmsMessagingTemplate.convertAndSend(queueName, userSettingDTO);
        logger.info("Message sent, User: " + userSettingDTO.getUserId());
    }
    @Async("MultiExecutor")
    public void SendNewPostNotification(NewPostNotificationDTO newPostNotificationDTO) throws JMSException {
        String topicName = NewPost.getQueueName();
        jmsMessagingTemplate.convertAndSend(topicName, newPostNotificationDTO);
        logger.info("Topic sent:" + topicName);
    }
}
