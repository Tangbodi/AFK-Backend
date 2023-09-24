package com.example.demo.Controller;

import com.example.demo.Constant.Enum.ReturnCode;
import com.example.demo.Model.DTO.UserLikeSaveDTO;
import com.example.demo.Service.Comments.CommentInfoService;
import com.example.demo.Service.MQ.MQSender;
import com.example.demo.Service.Posts.PostInfoService;
import com.example.demo.Service.Replies.ReplyInfoService;
import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.jms.JMSException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
    public ResponseEntity SetUserLikeSavePost(@Validated @RequestBody UserLikeSaveDTO userLikeSaveDTO, HttpServletRequest request) throws JMSException, InterruptedException {
        ApiResponse apiResponse;
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Sign in to make your opinion count");
            return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
        } else {
            if (userLikeSaveDTO.getStatus() == 0) {
                apiResponse = ApiResponse.success(1);
            } else {
                apiResponse = ApiResponse.success(0);
            }
            mqSender.SendLikeSaveMessage(userLikeSaveDTO, userId);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}
