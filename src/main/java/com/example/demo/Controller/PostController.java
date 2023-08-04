package com.example.demo.Controller;

import com.example.demo.Enum.ReturnCode;
import com.example.demo.Mapper.Repository.ReplyRepository;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.Entity.PostComment;
import com.example.demo.Model.Entity.PostReply;
import com.example.demo.Model.VO.CommentVO;
import com.example.demo.Model.VO.PostVO;
import com.example.demo.Model.VO.UserInfoVO;
import com.example.demo.Service.Comments.CommentService;
import com.example.demo.Service.Games.GameGenreService;
import com.example.demo.Service.IP.IpService;
import com.example.demo.Service.Posts.PostService;
import com.example.demo.Service.UsersInfo.UserInfoService;
import com.example.demo.Util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@RestController
@Validated
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    @Autowired
    private PostService postService;
    @Autowired
    private IpService ipService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private GameGenreService gameGenreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ReplyRepository replyRepository;

    @PostMapping(value = "/user/login/username/all-games-genres/genre/edit-post",produces = {"application/json;charset=UTF-8", "text/html;charset=UTF-8"})
    public ResponseEntity EditPost(HttpServletRequest request, @Validated @RequestBody PostDTO postDTO, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        logger.info("EditPost:::userId:::" + userId);
        String ipAddress = HttpUtils.getRequestIP(request);
        logger.info("EditPost:::ipAddress:::" + ipAddress);
        if (ipService.isValidInet4Address(ipAddress)) {
            logger.info("EditPost:::ipAddress is valid");
            String[] ip = ipAddress.split("\\.");
            logger.info("EditPost:::ipAddress split:::" + ip);
            Long ipvF = (Long.valueOf(ip[0]) << 24) + (Long.valueOf(ip[1]) << 16) + (Long.valueOf(ip[2]) << 8) + Long.valueOf(ip[3]);
            logger.info("EditPost:::ipvF:::" + ipvF);
            postDTO.setIpvFour(ipvF);
        } else if (ipService.isValidInet6Address(ipAddress)) {
            logger.info("EditPost:::ipAddress is valid");
            String[] ip = ipAddress.split(":");
            logger.info("EditPost:::ipvS:::" + Arrays.toString(ip));
            postDTO.setIpvSix(ip.toString());
        } else {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "Invalid IP Address");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        postDTO.setUserId(userId);
        postDTO.setCreatedAt(Instant.now());
        PostVO postVO = postService.EditPost(postDTO);
        ApiResponse apiResponse = ApiResponse.success(postVO);
        return ResponseEntity.ok(apiResponse);
    }
    @GetMapping("/user/login/username/all-games-genres/genre/post/{postId}")
    public ResponseEntity ShowPostPage(@PathVariable("postId") String postId) {
        List<Map<Short, Object>> commentList = commentService.GetAllCommentsByPostId(postId);
        List<String> commentIds = new ArrayList<>();
        for (Map<Short, Object> row : commentList) {
            System.out.println("Username: " + row.get("username"));
            System.out.println("Created At: " + row.get("created_at"));
            System.out.println("Content: " + row.get("content"));
            System.out.println("Comment Id: " + row.get("comment_id"));
            commentIds.add((String) row.get("comment_id"));
            System.out.println("-------------------------");
        }
        System.out.println("commentIds: " + commentIds);
        ApiResponse apiResponse = ApiResponse.success(commentList);
        return ResponseEntity.ok(apiResponse);
    }
    @GetMapping("/user/login/username/all-games-genres/genre/post/get-reply")
    public ResponseEntity GetAllReplyByCommentId(){
        List<String> commentIds = new ArrayList<>();
        commentIds.add("3aa8121c-8b6e-4773-8cc3-6cefd1c32275");
        commentIds.add("32c73440-d6ca-4beb-942a-86262c292605");

        List<Map<Short, Object>> replyList = replyRepository.findByCommentId(commentIds);
        ApiResponse apiResponse = ApiResponse.success(replyList);
        return ResponseEntity.ok(apiResponse);
    }
}
