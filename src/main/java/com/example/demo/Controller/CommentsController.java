package com.example.demo.Controller;

import com.example.demo.Enum.ReturnCode;
import com.example.demo.Model.DTO.CommentDTO;
import com.example.demo.Model.VO.CommentVO;
import com.example.demo.Service.Comments.CommentService;
import com.example.demo.Service.IP.IpService;
import com.example.demo.Service.Posts.PostService;
import com.example.demo.Util.ApiResponse;
import com.example.demo.Util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.Arrays;

@RestController
public class CommentsController {
    private static final Logger logger = LoggerFactory.getLogger(CommentsController.class);

    @Autowired
    private CommentService commentService;
    @Autowired
    private IpService ipService;
    @Autowired
    private PostService postService;

    @PostMapping("/all-games-genres/edit-comment")
    public ResponseEntity EditComment(HttpServletRequest request, @Validated @RequestBody CommentDTO commentDTO, HttpSession session) {
        logger.info("EditComment:::");
        String userId = (String) session.getAttribute("userId");
        ApiResponse apiResponse;
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Please login to share your opinion");
        } else {

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
                apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Invalid IP Address");
                return ResponseEntity.badRequest().body(apiResponse);
            }
            commentDTO.setFromUid(userId);
            commentDTO.setCreatedAt(Instant.now());
            CommentVO commentVO = commentService.SetComment(commentDTO);
            apiResponse = ApiResponse.success(commentVO);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

}
