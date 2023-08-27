package com.example.demo.Service.MQ;

import com.example.demo.Model.DTO.UserLikeSaveDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;

//@Component
//public class Produce {
//    private static final Logger logger = LoggerFactory.getLogger(Produce.class);
//    @Autowired
//    private KafkaTemplate<String, UserLikeSaveDTO > kafkaTemplate;
//
//    public void sendMessage(String topic, UserLikeSaveDTO userLikeSaveDTO) {
//        try {
//            SendResult<String, UserLikeSaveDTO> sendResult = kafkaTemplate.send(topic, userLikeSaveDTO).get();
//            if (sendResult.getRecordMetadata() != null) {
//                logger.info("生产者成功发送消息到" + sendResult.getProducerRecord().topic() + "-> " + sendResult.getProducerRecord().value().toString());
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//    }
//}
