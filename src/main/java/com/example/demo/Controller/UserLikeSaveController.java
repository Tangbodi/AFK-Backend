package com.example.demo.Controller;

import com.example.demo.Enum.ObjectNameEnum;
import com.example.demo.Enum.ReturnCode;
import com.example.demo.Model.DTO.ObjectUserDTO;
import com.example.demo.Model.DTO.UserLikeSaveDTO;
import com.example.demo.Service.Comments.CommentInfoService;
import com.example.demo.Service.MQ.MQSender;
import com.example.demo.Service.Posts.PostInfoService;
import com.example.demo.Service.Redis.RedisLikeSaveService;
import com.example.demo.Service.Replies.ReplyInfoService;
import com.example.demo.Service.UserLikeSave.UserLikeSaveService;
import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.jms.JMSException;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.*;

@RestController
@Validated
@RequestMapping("/all-games-genres")
public class UserLikeSaveController {
    private static final Logger logger = LoggerFactory.getLogger(UserLikeSaveController.class);
    @Autowired
    private PostInfoService postInfoService;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private CommentInfoService commentInfoService;
    @Autowired
    private ReplyInfoService replyInfoService;
    @PostMapping("/genre/user-like-save")
    public ResponseEntity SetUserLikeSavePost(@Validated @RequestBody UserLikeSaveDTO userLikeSaveDTO, HttpSession session) throws JMSException, InterruptedException {
        ApiResponse apiResponse;
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Sign in to make your opinion count");
            return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
        } else {
            apiResponse = ApiResponse.success(null);
            mqSender.SendSaveLikeMessage(userLikeSaveDTO, userId);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}
