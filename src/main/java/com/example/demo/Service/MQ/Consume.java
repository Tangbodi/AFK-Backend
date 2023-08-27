package com.example.demo.Service.MQ;

import com.example.demo.Model.DTO.UserLikeSaveDTO;
import com.example.demo.Util.LikeSaveStrategy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.stereotype.Component;


//@Component
//public class Consume {
//    @Autowired
//    private LikeSaveStrategy likeSaveStrategy;
//
//    private final Logger logger = LoggerFactory.getLogger(Consume.class);
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//
//    @KafkaListener(topics = {"like-save-redis"}, groupId = "afk-group")
//    public void consumeMessage(ConsumerRecord<String, String> userLikeSaveDTORecord) {
//        try {
//            UserLikeSaveDTO userLikeSaveDTO = objectMapper.readValue(userLikeSaveDTORecord.value(), UserLikeSaveDTO.class);
//            logger.info("消费者消费topic:{} partition:{}的消息 -> {}", userLikeSaveDTORecord.topic(), userLikeSaveDTORecord.partition(), userLikeSaveDTO.toString());
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//    }
//}
