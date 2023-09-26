package com.example.demo.Service.MQ;

import com.example.demo.Model.DTO.*;
import com.example.demo.Service.Redis.RedisStrategy;
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

    @JmsListener(destination = "user-registration-redis", containerFactory = "activeMQFactory")
    public void UserRegistrationHandle(Message message) {
        try {
            ActiveMQObjectMessage activeMqObjectMessage = (ActiveMQObjectMessage) message;
            UserRegisterDTO userRegisterDTO = (UserRegisterDTO) activeMqObjectMessage.getObject();
            try {
                redisStrategy.UserRegistrationStrategy(userRegisterDTO);
                logger.info("User-registration consumer record: User: {}", userRegisterDTO.getUserId());
            } catch (Exception e) {
                logger.error("Error processing message for User: " + userRegisterDTO.getUserId(), e);
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

    @JmsListener(destination = "like-save-redis", containerFactory = "activeMQFactory")
    public void LikeSaveHandle(Message message) {
        try {
            ActiveMQObjectMessage activeMqObjectMessage = (ActiveMQObjectMessage) message;
            UserLikeSaveDTO userLikeSaveDTO = (UserLikeSaveDTO) activeMqObjectMessage.getObject();
            Integer status = userLikeSaveDTO.getStatus();

            try {
                //save game active
                if (userLikeSaveDTO.getTypeId() == 4) {
                    redisStrategy.UserFavoriteGameStrategy(userLikeSaveDTO);
                } else {
                    redisStrategy.LikeSaveStrategy(userLikeSaveDTO);
                    //if user like save, then send mention message
                    if (userLikeSaveDTO.getStatus() == 1) {
                        redisStrategy.LikeSaveMentionStrategy(userLikeSaveDTO);
                    } else {
                        //
                    }
                    logger.info("Like-save consumer record: User: {}, status: {}, objectId: {}",
                            userLikeSaveDTO.getUserId(), status, userLikeSaveDTO.getObjectId());
                }
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
    public void CommentCountHandle(Message message) {
        try {
            ActiveMQObjectMessage activeMqObjectMessage = (ActiveMQObjectMessage) message;
            CommentReplyDTO commentReplyDTO = (CommentReplyDTO) activeMqObjectMessage.getObject();

            try {
                redisStrategy.CommentCountStrategy(commentReplyDTO);
                logger.info("Comment-count consumer record: User: {}, postId: {}",
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

    @JmsListener(destination = "reply-count-redis", containerFactory = "activeMQFactory")
    public void ReplyCountHandle(Message message) {
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
    public void SendMentionMessageHandle(Message message) {
        try {
            ActiveMQObjectMessage activeMqObjectMessage = (ActiveMQObjectMessage) message;
            Long userId = (Long) activeMqObjectMessage.getObject();
            try {
                redisStrategy.MessageMentionStrategy(userId);
                logger.info("Message-mention consumer record: User: {}", userId);
            } catch (Exception e) {
                logger.error("Error processing message for User: " + userId, e);
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

    @JmsListener(destination = "update-email-redis", containerFactory = "activeMQFactory")
    public void UpdateEmailHandle(Message message) {
        try {
            ActiveMQObjectMessage activeMqObjectMessage = (ActiveMQObjectMessage) message;
            EmailDTO emailDTO = (EmailDTO) activeMqObjectMessage.getObject();
            try {
                redisStrategy.UpdateEmailStrategy(emailDTO);
                logger.info("Update-email consumer record: User: {}", emailDTO.getUserId());
            } catch (Exception e) {
                logger.error("Error processing message for User: " + emailDTO.getUserId(), e);
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

    @JmsListener(destination = "forgot-password-redis", containerFactory = "activeMQFactory")
    public void ForgotPasswordHandle(Message message) {
        try {
            ActiveMQObjectMessage activeMqObjectMessage = (ActiveMQObjectMessage) message;
            EmailDTO emailDTO = (EmailDTO) activeMqObjectMessage.getObject();
            try {
                redisStrategy.ForgotPasswordStrategy(emailDTO);
                logger.info("Forgot-password consumer record: User: {}", emailDTO.getUserId());
            } catch (Exception e) {
                logger.error("Error processing message for User: " + emailDTO.getUserId(), e);
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
//    @JmsListener(destination = "user-setting-redis", containerFactory = "activeMQFactory")
//    public void UserSettingHandle(Message message){
//        try{
//            ActiveMQObjectMessage activeMqObjectMessage = (ActiveMQObjectMessage) message;
//            UserSettingDTO userSettingDTO = (UserSettingDTO) activeMqObjectMessage.getObject();
//            try {
//                redisStrategy.UserSettingStrategy(userSettingDTO);
//                logger.info("User-setting consumer record: User: {}", userSettingDTO.getUserId());
//            } catch (Exception e) {
//                logger.error("Error processing message for User: " + userSettingDTO.getUserId(), e);
//                // Optionally, throw a custom exception or take other appropriate action
//            }
//        } catch (JMSException e) {
//            logger.error("JMS Exception while processing message: " + e.getMessage(), e);
//            // Optionally, throw a custom exception or take other appropriate action
//        } catch (Exception e) {
//            logger.error("Unhandled Exception while processing message: " + e.getMessage(), e);
//            // Optionally, throw a custom exception or take other appropriate action
//        }
//    }
}
