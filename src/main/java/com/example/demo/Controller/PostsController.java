package com.example.demo.Controller;

import com.example.demo.Enum.ReturnCode;
import com.example.demo.Mapper.Repository.ReplyRepository;
import com.example.demo.Model.DTO.GameGenreMapIdDTO;
import com.example.demo.Model.DTO.GetPostDTO;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.VO.*;
import com.example.demo.Service.Comments.CommentService;
import com.example.demo.Service.Games.GameGenreMapService;
import com.example.demo.Service.Games.GameGenreService;
import com.example.demo.Service.IP.IpService;
import com.example.demo.Service.Posts.PostGameMapService;
import com.example.demo.Service.Posts.PostInfoService;
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
import java.util.*;


@RestController
@Validated
public class PostsController {
    private static final Logger logger = LoggerFactory.getLogger(PostsController.class);
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
    @Autowired
    private PostGameMapService postGameMapService;
    @Autowired
    private PostInfoService postInfoService;
    @Autowired
    private GameGenreMapService gameGenreMapService;

    @GetMapping("/all-games-genres/posts")
    public ResponseEntity ShowAllPostInfoWithOneGame(@RequestParam(value = "game") Short gameId, @RequestParam(value = "genre") Byte genreId) {
        ApiResponse apiResponse;
        GameGenreMapIdDTO gameGenreMapIdDTO = new GameGenreMapIdDTO();
        gameGenreMapIdDTO.setGameId(gameId);
        gameGenreMapIdDTO.setGenreId(genreId);
        if(!GameIdValidator.CheckGameId(gameId) || !GenreIdValidator.CheckGenreId(genreId)){
            apiResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "Post not found");
        }else if(gameGenreMapService.FindGamesGenresMapById(gameGenreMapIdDTO) == null){
            apiResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "Post not found");
        }else{
            List<PostInfoVO> showPostVOList = postInfoService.GetAllPostInfoWithOneGame(gameGenreMapIdDTO);
            apiResponse = ApiResponse.success(showPostVOList);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @GetMapping("/all-games-genres/post-body")
    public ResponseEntity ShowPostContent(HttpServletRequest request, @RequestParam(value = "game") Short gameId, @RequestParam(value = "genre") Byte genreId, @RequestParam(value = "post") String postId) {
        ApiResponse apiResponse;
        GameGenreMapIdDTO gameGenreMapIdDTO = new GameGenreMapIdDTO();
        gameGenreMapIdDTO.setGameId(gameId);
        gameGenreMapIdDTO.setGenreId(genreId);
        if (!PostIdValidator.CheckPostId(postId) || !GameIdValidator.CheckGameId(gameId) || !GenreIdValidator.CheckGenreId(genreId)) {
            apiResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "Post not found");
        } else if(gameGenreMapService.FindGamesGenresMapById(gameGenreMapIdDTO) == null){
            apiResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "Post not found");
        } else {
            GetPostDTO getPostDTO = new GetPostDTO();
            getPostDTO.setPostId(postId);
            getPostDTO.setGameId(gameId);
            getPostDTO.setGenreId(genreId);

            ShowPostVO showPostVO = postService.GetPost(getPostDTO);
            if (showPostVO == null) {
                apiResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "Post not found");
            } else {
                apiResponse = ApiResponse.success(showPostVO);
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @GetMapping("/all-games-genres/comments-replies")
    public ResponseEntity ShowAllCommentsAndReplies(@RequestParam(value = "game") Short gameId, @RequestParam(value = "genre") Byte genreId, @RequestParam(value = "post") String postId) {
        ApiResponse apiResponse;
        GameGenreMapIdDTO gameGenreMapIdDTO = new GameGenreMapIdDTO();
        gameGenreMapIdDTO.setGameId(gameId);
        gameGenreMapIdDTO.setGenreId(genreId);
        if (!PostIdValidator.CheckPostId(postId) || !GameIdValidator.CheckGameId(gameId) || !GenreIdValidator.CheckGenreId(genreId)) {
            apiResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "Post not found");
        } else if(gameGenreMapService.FindGamesGenresMapById(gameGenreMapIdDTO) == null){
            apiResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "Post not found");
        }else {
            GetPostDTO getPostDTO = new GetPostDTO();
            getPostDTO.setPostId(postId);
            getPostDTO.setGameId(gameId);
            getPostDTO.setGenreId(genreId);
            ShowPostVO showPostVO = postService.GetPost(getPostDTO);
            if (showPostVO == null) {
                apiResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "Post not found");
            } else {
                List<Map<Short, Object>> commentList = commentService.GetAllCommentsByPostId(postId);
                List<String> commentIds = new ArrayList<>();
                for (Map<Short, Object> row : commentList) {
                    commentIds.add((String) row.get("comment_id"));
                }
                List<Map<Short, Object>> replyList = replyRepository.findByCommentId(commentIds);
                List<List<Object>> res = new ArrayList<>();

                for (Map<Short, Object> commentRow : commentList) {
                    String commentId = (String) commentRow.get("comment_id");
                    List<Map<String, String>> replies = new ArrayList<>();
                    for (Map<Short, Object> replyRow : replyList) {
                        Map<String, String> repliesMap = new HashMap<>();
                        String commentId2 = (String) replyRow.get("comment_id");
                        if (commentId.equals(commentId2)) {
                            repliesMap.put("from_username", (String) replyRow.get("from_username"));
                            repliesMap.put("to_username", (String) replyRow.get("to_username"));
                            repliesMap.put("content", (String) replyRow.get("content"));
                            repliesMap.put("created_at", replyRow.get("created_at").toString());
                            replies.add(repliesMap);
                        } else {
                            continue;
                        }
                    }
                    List<Object> combinedList = new ArrayList<>();
                    combinedList.add(commentRow);
                    combinedList.add(replies);
                    res.add(combinedList);
                }
                apiResponse = ApiResponse.success(res);
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PostMapping(value = "/all-games-genres/edit-post", produces = {"application/json;charset=UTF-8", "text/html;charset=UTF-8"})
    public ResponseEntity EditPost(HttpServletRequest request, @Validated @RequestBody PostDTO postDTO, HttpSession session) {
        ApiResponse apiResponse;
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        }  else {
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
                    apiResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "Invalid IP Address");
                    return ResponseEntity.badRequest().body(apiResponse);
                }
                postDTO.setUserId(userId);
                postDTO.setCreatedAt(Instant.now());
                PostSavedVO postSavedVO = postService.EditPost(postDTO);
                apiResponse = ApiResponse.success(postSavedVO);
            }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @GetMapping("/all-games-genres/genre/latest-posts")
    public ResponseEntity ShowLatestPosts() {
        ApiResponse apiResponse;
        List<LatestPostVO> latestPosts = postGameMapService.ShowLatestPosts();
        apiResponse = ApiResponse.success(latestPosts);
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @GetMapping("/all-games-genres/genre/popular-posts")
    public ResponseEntity ShowPopularPosts() {
        ApiResponse apiResponse;
        List<PopularPostVO> popularPosts = postInfoService.GetMostPopularPosts();
        apiResponse = ApiResponse.success(popularPosts);
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}
