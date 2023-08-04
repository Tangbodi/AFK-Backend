package com.example.demo.Controller;

import com.example.demo.Enum.ReturnCode;
import com.example.demo.Model.DTO.CommentDTO;
import com.example.demo.Model.VO.CommentVO;
import com.example.demo.Service.Comments.CommentService;
import com.example.demo.Service.IP.IpService;
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
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;
    @Autowired
    private IpService ipService;

    @PostMapping("/user/login/username/all-games-genres/genre/post/{postId}/edit-comment")
    public ResponseEntity EditComment(HttpServletRequest request, @PathVariable("postId") String postId, @RequestBody CommentDTO commentDTO, HttpSession session) {
        logger.info("EditComment:::");
        String userId = (String) session.getAttribute("userId");
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
            commentDTO.setIpvFour(ipvF);
        } else if (ipService.isValidInet6Address(ipAddress)) {
            logger.info("EditPost:::ipAddress is valid");
            String[] ip = ipAddress.split(":");
            logger.info("EditPost:::ipvS:::" + Arrays.toString(ip));
            commentDTO.setIpvSix(ip.toString());
        } else {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "Invalid IP Address");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        commentDTO.setPostId(postId);
        commentDTO.setFromUid(userId);
        commentDTO.setCreatedAt(Instant.now());
        CommentVO commentVO = commentService.SetComment(commentDTO);
        ApiResponse apiResponse = ApiResponse.success(commentVO);
        return ResponseEntity.ok(apiResponse);
    }
}
