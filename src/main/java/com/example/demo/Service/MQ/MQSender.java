package com.example.demo.Service.MQ;

import com.example.demo.Model.DTO.UserLikeSaveDTO;
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
    private Queue queue;
    @Async("MultiExecutor")
    public void SendMessage(UserLikeSaveDTO userLikeSaveDTO,Long userId) throws JMSException, InterruptedException {
        String queueName = queue.getQueueName();
        userLikeSaveDTO.setUserId(userId);
        userLikeSaveDTO.setCreatedAt(Instant.now());
        jmsMessagingTemplate.convertAndSend(queueName, userLikeSaveDTO);
        logger.info("Message sent, User: " + userLikeSaveDTO.getUserId() +"," +
                "status: " + userLikeSaveDTO.getStatus() + "，" +
                "objectId: " + userLikeSaveDTO.getObjectId());
    }
}
