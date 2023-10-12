package com.example.demo.Service.MQ;

import com.example.demo.Model.DTO.NewPostNotificationDTO;
import com.example.demo.Model.DTO.PostDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Topic;

@Service
public class GameSubProducer {
    private static final Logger logger = LoggerFactory.getLogger(GameSubProducer.class);
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Topic NewPost;

    @Async("MultiExecutor")
    public void SendGameSubNotification(NewPostNotificationDTO newPostNotificationDTO) throws JMSException {
        String topicName = NewPost.getTopicName();
        jmsTemplate.convertAndSend(topicName, newPostNotificationDTO);
        logger.info("Topic sent:" + topicName);
    }
}
