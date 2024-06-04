package com.example.demo.Controller;

import com.example.demo.Constant.Enum.ReturnCode;
import com.example.demo.Model.DTO.UserLikeSaveDTO;
import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

//@RestController
//public class TestController {
//    @Autowired
//    private KafkaTemplate<String, Object> kafkaTemplate;
//    @Value("${kafka.like-save}")
//    private String likeSaveTopic;
//
//    //http:localhost:8080/api/v1/publish?message=hello
//    @PostMapping("/publish")
//    public ResponseEntity<String> publish(@Validated @RequestBody UserLikeSaveDTO userLikeSaveDTO, HttpServletRequest request){
//        ApiResponse apiResponse;
//        kafkaTemplate.send(likeSaveTopic, userLikeSaveDTO);
//        return ResponseEntity.ok("Published to like-save-redis successfully");
//    }
//}
