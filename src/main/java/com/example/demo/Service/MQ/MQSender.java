package com.example.demo.Service.MQ;

import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.DTO.UserLikeSaveDTO;
import com.example.demo.Model.VO.MessageVO;
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
    @Async("MultiExecutor")
    public void SendSaveLikeMessage(UserLikeSaveDTO userLikeSaveDTO,Long userId) throws JMSException, InterruptedException {
        String queueName = LikeSaveQueue.getQueueName();
        userLikeSaveDTO.setUserId(userId);
        userLikeSaveDTO.setCreatedAt(Instant.now());
        jmsMessagingTemplate.convertAndSend(queueName, userLikeSaveDTO);
        logger.info("Message sent, User: " + userLikeSaveDTO.getUserId() +"," +
                "status: " + userLikeSaveDTO.getStatus() + "，" +
                "objectId: " + userLikeSaveDTO.getObjectId());
    }
    @Async("MultiExecutor")
    public void SendCommentCountMessage(CommentReplyDTO commentReplyDTO) throws JMSException, InterruptedException {
        String queueName = CommentCountQueue.getQueueName();
        jmsMessagingTemplate.convertAndSend(queueName, commentReplyDTO);
        logger.info("Message sent, User: " + commentReplyDTO.getFromUid() +"," +
                "commentId: " + commentReplyDTO.getCommentId() + "," +
                "postId: " + commentReplyDTO.getPostId());
    }

    @Async("MultiExecutor")
    public void SendReplyCountMessage(CommentReplyDTO commentReplyDTO) throws JMSException, InterruptedException {
        String queueName = ReplyCountQueue.getQueueName();
        jmsMessagingTemplate.convertAndSend(queueName, commentReplyDTO);
        logger.info("Message sent, User: " + commentReplyDTO.getFromUid() +"," +
                "replyId: " + commentReplyDTO.getReplyId() + "," +
                "postId: " + commentReplyDTO.getPostId());
    }

    @Async("MultiExecutor")
    public void SendMentionMessage(MessageVO messageVO) throws JMSException {
        String queueName = MessageMentionQueue.getQueueName();
        jmsMessagingTemplate.convertAndSend(queueName, messageVO);
        logger.info("Message sent, User: " + messageVO.getToUid());
    }
}
