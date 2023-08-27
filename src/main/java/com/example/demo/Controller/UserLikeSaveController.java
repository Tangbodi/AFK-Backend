package com.example.demo.Controller;

import com.example.demo.Enum.ReturnCode;
import com.example.demo.Model.DTO.UserLikeSaveDTO;
import com.example.demo.Service.Redis.RedisLikeSaveService;
import com.example.demo.Util.ApiResponse;
import com.example.demo.Service.MQ.MQSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import javax.servlet.http.HttpSession;

@RestController
@Validated
@RequestMapping("/all-games-genres")
public class UserLikeSaveController {
    private static final Logger logger = LoggerFactory.getLogger(UserLikeSaveController.class);
    @Autowired
    private RedisLikeSaveService redisLikeSaveService;
    @Autowired
    private MQSender mqSender;

    @PostMapping("/genre/like-save")
    public ResponseEntity SetUserLikeSavePost(@Validated @RequestBody UserLikeSaveDTO userLikeSaveDTO, HttpSession session) throws JMSException, InterruptedException {
        ApiResponse apiResponse;
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Sign in to make your opinion count");
            return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
        } else {
            switch (userLikeSaveDTO.getType()) {
                case "like":
                    apiResponse = ApiResponse.success(null);
                    mqSender.SendMessage(userLikeSaveDTO,userId);
                    break;
                case "save":
                    apiResponse = ApiResponse.success(null);
                    break;
                default:
                    apiResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "Invalid type");
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}
