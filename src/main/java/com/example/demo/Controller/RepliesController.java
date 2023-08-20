package com.example.demo.Controller;

import com.example.demo.Enum.ReturnCode;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.VO.ReplyVO;
import com.example.demo.Service.IP.IpService;
import com.example.demo.Service.Message.MessageService;
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

@RestController
@RequestMapping("/all-games-genres")
public class RepliesController {
    private static final Logger logger = LoggerFactory.getLogger(RepliesController.class);
    private static final String MESSAGE_MENTION_KEY = "UNREAD:";

    @Autowired
    private ReplyService replyService;
    @Autowired
    private IpService ipService;
    @Autowired
    private PostService postService;
    @Autowired
    private RedisMessageService redisMessageService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MessageService messageService;


    @PostMapping("/edit-reply")
    public ResponseEntity EditReply(HttpServletRequest request, @Validated @RequestBody CommentReplyDTO commentReplyDTO, HttpSession session) {
        ApiResponse apiResponse;
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Sign in to share your opinion");
        } else {
            String ipAddress = HttpUtils.getRequestIP(request);
            logger.info("ipAddress:::" + ipAddress);
            if (ipService.isValidInet4Address(ipAddress)) {
                logger.info("ipAddress is valid");
                String[] ip = ipAddress.split("\\.");
                logger.info("ipAddress split:::" + ip);
                Long ipvF = (Long.valueOf(ip[0]) << 24) + (Long.valueOf(ip[1]) << 16) + (Long.valueOf(ip[2]) << 8) + Long.valueOf(ip[3]);
                logger.info("ipvF:::" + ipvF);
                commentReplyDTO.setIpvFour(ipvF);
            } else if (ipService.isValidInet6Address(ipAddress)) {
                logger.info("ipAddress is valid");
                String[] ip = ipAddress.split(":");
                logger.info("ipvS:::" + Arrays.toString(ip));
                commentReplyDTO.setIpvSix(ip.toString());
            } else {
                apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Invalid IP Address");
                return ResponseEntity.badRequest().body(apiResponse);
            }
            commentReplyDTO.setFromUid(userId);
            ReplyVO replyVO = replyService.SetReply(commentReplyDTO);
            //Set mention message after saved reply
            if (replyVO != null && !commentReplyDTO.getToUid().equals(commentReplyDTO.getFromUid())) {
                messageService.SetMessage(commentReplyDTO);
                redisMessageService.SetUserReadStatus(commentReplyDTO.getToUid());
            } else {
               //
            }
            apiResponse = ApiResponse.success(replyVO);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

}
