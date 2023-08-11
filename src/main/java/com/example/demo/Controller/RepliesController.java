package com.example.demo.Controller;

import com.example.demo.Enum.ReturnCode;
import com.example.demo.Model.DTO.ReplyDTO;
import com.example.demo.Model.VO.ReplyVO;
import com.example.demo.Service.IP.IpService;
import com.example.demo.Service.Replies.ReplyService;
import com.example.demo.Util.ApiResponse;
import com.example.demo.Util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.Arrays;

@RestController
public class RepliesController {
    private static final Logger logger = LoggerFactory.getLogger(RepliesController.class);

    @Autowired
    private ReplyService replyService;
    @Autowired
    private IpService ipService;
    @PostMapping("/user/login/username/all-games-genres/genre/post/{postId}/edit-reply")
    public ResponseEntity EditReply(HttpServletRequest request, @PathVariable("postId") String postId, @RequestBody ReplyDTO replyDTO, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        logger.info("EditReply:::replyDTO:::" + replyDTO);
        if (userId == null) {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
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
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "Invalid IP Address");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        replyDTO.setFromUid(userId);
        replyDTO.setCreatedAt(Instant.now());
        ReplyVO replyVO = replyService.SetReply(replyDTO);
        ApiResponse apiResponse = ApiResponse.success(replyVO);
        return ResponseEntity.ok(apiResponse);
    }
    @GetMapping("/user/login/username/all-games-genres/genre/newest-replies")
    public ResponseEntity ShowNewestReplies() {
        ApiResponse apiResponse = ApiResponse.success(replyService.GetNewestReplies());
        return ResponseEntity.ok(apiResponse);
    }
}
