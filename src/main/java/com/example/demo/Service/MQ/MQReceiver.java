package com.example.demo.Service.MQ;

import com.example.demo.Model.DTO.UserLikeSaveDTO;
import com.example.demo.Util.LikeSaveStrategy;
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
    private LikeSaveStrategy likeSaveStrategy;
    @JmsListener(destination = "like-save-redis", containerFactory = "activeMQFactory")
    public void handle(Message message) {
        try {
            ActiveMQObjectMessage activeMqObjectMessage = (ActiveMQObjectMessage) message;
            UserLikeSaveDTO userLikeSaveDTO = (UserLikeSaveDTO) activeMqObjectMessage.getObject();
            Integer status = userLikeSaveDTO.getStatus();

            try {
                likeSaveStrategy.StartStrategy(userLikeSaveDTO);
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
}
