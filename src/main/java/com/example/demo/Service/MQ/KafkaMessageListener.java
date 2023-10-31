package com.example.demo.Service.MQ;

import com.example.demo.Model.DTO.UserLikeSaveDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Optional;

//@Component
//@Slf4j
//public class KafkaMessageListener {
//    @KafkaListener(topics = "${kafka.like-save}", groupId = "afk-group", concurrency = "1")
//    public void LikeSaveHandle(UserLikeSaveDTO userLikeSaveDTO) {
//        try {
//            // Process the message from the likeSaveTopic
//            log.info("Message received from likeSaveTopic: {}", userLikeSaveDTO);
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info("KafkaMessageListener error: {}", e.getMessage());
//        }
//    }
//}
