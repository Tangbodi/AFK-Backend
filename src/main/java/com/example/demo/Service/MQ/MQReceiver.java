package com.example.demo.Service.MQ;

import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.DTO.UserLikeSaveDTO;
import com.example.demo.Model.VO.MessageVO;
import com.example.demo.Util.RedisStrategy;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;

@Component
public class MQReceiver {
    private static final Logger logger = LoggerFactory.getLogger(MQReceiver.class);
    @Autowired
    private RedisStrategy redisStrategy;
    @JmsListener(destination = "like-save-redis", containerFactory = "activeMQFactory")
    public void LikeSaveHandle(Message message) {
        try {
            ActiveMQObjectMessage activeMqObjectMessage = (ActiveMQObjectMessage) message;
            UserLikeSaveDTO userLikeSaveDTO = (UserLikeSaveDTO) activeMqObjectMessage.getObject();
            Integer status = userLikeSaveDTO.getStatus();

            try {
                redisStrategy.LikeSaveStrategy(userLikeSaveDTO);
                logger.info("Like-save consumer record: User: {}, status: {}, objectId: {}",
                        userLikeSaveDTO.getUserId(), status, userLikeSaveDTO.getObjectId());
            } catch (Exception e) {
                logger.error("Error processing message for User: " + userLikeSaveDTO.getUserId(), e);
                // Optionally, throw a custom exception or take other appropriate action
            }
        } catch (JMSException e) {
            logger.error("JMS Exception while processing message: " + e.getMessage(), e);
            // Optionally, throw a custom exception or take other appropriate action
        } catch (Exception e) {
            logger.error("Unhandled Exception while processing message: " + e.getMessage(), e);
            // Optionally, throw a custom exception or take other appropriate action
        }
    }

    @JmsListener(destination = "comment-count-redis", containerFactory = "activeMQFactory")
    public void CommentCountHandle(Message message){
        try {
            ActiveMQObjectMessage activeMqObjectMessage = (ActiveMQObjectMessage) message;
            CommentReplyDTO commentReplyDTO = (CommentReplyDTO) activeMqObjectMessage.getObject();

            try {
                redisStrategy.CommentCountStrategy(commentReplyDTO);
                logger.info("Comment-count consumer record: User: {}, postId: {}",
                        commentReplyDTO.getFromUid(),commentReplyDTO.getPostId());
            } catch (Exception e) {
                logger.error("Error processing message for User: " + commentReplyDTO.getFromUid(), e);
                // Optionally, throw a custom exception or take other appropriate action
            }
        } catch (JMSException e) {
            logger.error("JMS Exception while processing message: " + e.getMessage(), e);
            // Optionally, throw a custom exception or take other appropriate action
        } catch (Exception e) {
            logger.error("Unhandled Exception while processing message: " + e.getMessage(), e);
            // Optionally, throw a custom exception or take other appropriate action
        }
    }

    @JmsListener(destination = "reply-count-redis", containerFactory = "activeMQFactory")
    public void ReplyCountHandle(Message message){
        try {
            ActiveMQObjectMessage activeMqObjectMessage = (ActiveMQObjectMessage) message;
            CommentReplyDTO commentReplyDTO = (CommentReplyDTO) activeMqObjectMessage.getObject();
            try {
                redisStrategy.ReplyCountStrategy(commentReplyDTO);
                logger.info("Reply-count consumer record: User: {}, postId: {}",
                        commentReplyDTO.getFromUid(), commentReplyDTO.getPostId());
            } catch (Exception e) {
                logger.error("Error processing message for User: " + commentReplyDTO.getFromUid(), e);
                // Optionally, throw a custom exception or take other appropriate action
            }
        } catch (JMSException e) {
            logger.error("JMS Exception while processing message: " + e.getMessage(), e);
            // Optionally, throw a custom exception or take other appropriate action
        } catch (Exception e) {
            logger.error("Unhandled Exception while processing message: " + e.getMessage(), e);
            // Optionally, throw a custom exception or take other appropriate action
        }
    }
    @JmsListener(destination = "message-mention-redis", containerFactory = "activeMQFactory")
    public void MessageMentionHandle(Message message){
        try {
            ActiveMQObjectMessage activeMqObjectMessage = (ActiveMQObjectMessage) message;
            MessageVO messageVO = (MessageVO) activeMqObjectMessage.getObject();
            try {
                redisStrategy.MessageMentionStrategy(messageVO);
                logger.info("Message-mention consumer record: User: {}", messageVO.getToUid());
            } catch (Exception e) {
                logger.error("Error processing message for User: " + messageVO.getToUid(), e);
                // Optionally, throw a custom exception or take other appropriate action
            }
        } catch (JMSException e) {
            logger.error("JMS Exception while processing message: " + e.getMessage(), e);
            // Optionally, throw a custom exception or take other appropriate action
        } catch (Exception e) {
            logger.error("Unhandled Exception while processing message: " + e.getMessage(), e);
            // Optionally, throw a custom exception or take other appropriate action
        }
    }
}
