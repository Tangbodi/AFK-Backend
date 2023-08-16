package com.example.demo.Controller;

import com.example.demo.Enum.ReturnCode;
import com.example.demo.Model.DTO.ReplyDTO;
import com.example.demo.Model.Entity.Message;
import com.example.demo.Model.VO.MessageVO;
import com.example.demo.Model.VO.ReplyVO;
import com.example.demo.Service.IP.IpService;
import com.example.demo.Service.Posts.PostCommentService;
import com.example.demo.Service.Posts.PostService;
import com.example.demo.Service.Redis.RedisMessageService;
import com.example.demo.Service.Redis.RedisService;
import com.example.demo.Service.Replies.ReplyService;
import com.example.demo.Util.ApiResponse;
import com.example.demo.Util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
public class RepliesController {
    private static final Logger logger = LoggerFactory.getLogger(RepliesController.class);
    private static final String MESSAGE_MENTION_KEY = "UNREAD:";

    @Autowired
    private ReplyService replyService;
    @Autowired
    private PostCommentService postCommentService;
    @Autowired
    private IpService ipService;
    @Autowired
    private PostService postService;
    @Autowired
    private RedisMessageService redisMessageService;
    @Autowired
    private RedisService redisService;


    @PostMapping("/all-games-genres/edit-reply")
    public ResponseEntity EditReply(HttpServletRequest request, @Validated @RequestBody ReplyDTO replyDTO, HttpSession session) {
        ApiResponse apiResponse;
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        } else {
            String ipAddress = HttpUtils.getRequestIP(request);
            logger.info("EditPost:::ipAddress:::" + ipAddress);
            if (ipService.isValidInet4Address(ipAddress)) {
                logger.info("EditPost:::ipAddress is valid");
                String[] ip = ipAddress.split("\\.");
                logger.info("EditPost:::ipAddress split:::" + ip);
                Long ipvF = (Long.valueOf(ip[0]) << 24) + (Long.valueOf(ip[1]) << 16) + (Long.valueOf(ip[2]) << 8) + Long.valueOf(ip[3]);
                logger.info("EditPost:::ipvF:::" + ipvF);
                replyDTO.setIpvFour(ipvF);
            } else if (ipService.isValidInet6Address(ipAddress)) {
                logger.info("EditPost:::ipAddress is valid");
                String[] ip = ipAddress.split(":");
                logger.info("EditPost:::ipvS:::" + Arrays.toString(ip));
                replyDTO.setIpvSix(ip.toString());
            } else {
                apiResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "Invalid IP Address");
                return ResponseEntity.badRequest().body(apiResponse);
            }
            replyDTO.setFromUid(userId);
            replyDTO.setCreatedAt(Instant.now());
            ReplyVO replyVO = replyService.SetReply(replyDTO);
            if (replyVO != null) {
                Message message = replyService.SetMessage(replyDTO);
                replyService.SetMessageUserMap(message);
                redisMessageService.SetUserReadStatus(replyDTO.getToUid());
            } else {
                //
            }
            apiResponse = ApiResponse.success(replyVO);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @GetMapping("/all-games-genres/unread-reply")
    public ResponseEntity GetUnreadMessages(HttpSession session) {
        ApiResponse apiResponse;
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.success(null);
        } else {
            //redisService.CacheExists(MESSAGE_MENTION_KEY+userId)
            List<MessageVO> messageVOList = replyService.GetUnreadMessageViaMessageUserMap(userId);
            apiResponse = ApiResponse.success(messageVOList);

        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PutMapping("/all-games-genres/read-reply")
    public ResponseEntity ReadMessages(HttpSession session) {
        ApiResponse apiResponse;
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        } else {
            replyService.UpdateMessageUserMap(userId);
            apiResponse = ApiResponse.success(null);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}
