package com.example.demo.Controller;

import com.example.demo.Constant.Enum.ReturnCode;
import com.example.demo.Model.DTO.CommentReplyDTO;
import com.example.demo.Model.VO.CommentSavedVO;
import com.example.demo.Service.Comments.CommentService;
import com.example.demo.Service.IP.IpService;
import com.example.demo.Service.MQ.MQSender;
import com.example.demo.Service.Message.MessageService;
import com.example.demo.Service.Posts.PostService;
import com.example.demo.Service.Redis.RedisMessageService;
import com.example.demo.Util.ApiResponse;
import com.example.demo.Util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

@RestController
@RequestMapping("/all-games-genres")
public class CommentsController {
    private static final Logger logger = LoggerFactory.getLogger(CommentsController.class);

    @Autowired
    private CommentService commentService;
    @Autowired
    private IpService ipService;
    @Autowired
    private PostService postService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private RedisMessageService redisMessageService;
    @Autowired
    private MQSender mqSender;
    @PostMapping("/edit-comment")
    public ResponseEntity EditComment(HttpServletRequest request, @Validated @RequestBody CommentReplyDTO commentReplyDTO, HttpSession session) {
        logger.info("EditComment");
        Long userId = (Long) request.getSession().getAttribute("userId");
        ApiResponse apiResponse;
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to share your opinion");
        } else {

            String ipAddress = HttpUtils.getRequestIP(request);
            logger.info("ipAddress:{}" + ipAddress);
            if (ipService.isValidInet4Address(ipAddress)) {
                logger.info("ipAddress is valid");
                String[] ip = ipAddress.split("\\.");
                logger.info("ipAddress split:{}" + ip);
                Long ipvF = (Long.valueOf(ip[0]) << 24) + (Long.valueOf(ip[1]) << 16) + (Long.valueOf(ip[2]) << 8) + Long.valueOf(ip[3]);
                logger.info("ipvF:{}" + ipvF);
                commentReplyDTO.setIpvFour(ipvF);
            } else if (ipService.isValidInet6Address(ipAddress)) {
                logger.info("ipAddress is valid");
                String[] ip = ipAddress.split(":");
                logger.info("ipvS:{}" + Arrays.toString(ip));
                commentReplyDTO.setIpvSix(ip.toString());
            } else {
                apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Invalid IP Address");
                return ResponseEntity.badRequest().body(apiResponse);
            }
            commentReplyDTO.setFromUid(userId);
            commentReplyDTO.setFromUsername((String) request.getSession().getAttribute("username"));
            CommentSavedVO commentSavedVO = commentService.SaveComment(commentReplyDTO);
            if (commentSavedVO != null) {
                apiResponse = ApiResponse.success(commentSavedVO);
            } else {
                apiResponse = ApiResponse.error(ReturnCode.RC500.getCode(), "Failed to save comment");
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

}
