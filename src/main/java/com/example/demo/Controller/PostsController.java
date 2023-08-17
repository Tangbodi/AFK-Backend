package com.example.demo.Controller;

import com.example.demo.Enum.ReturnCode;
import com.example.demo.Mapper.Repository.ReplyRepository;
import com.example.demo.Model.DTO.GameGenreMapIdDTO;
import com.example.demo.Model.DTO.GetPostDTO;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.DTO.TypeDTO;
import com.example.demo.Model.VO.*;
import com.example.demo.Service.Comments.CommentService;
import com.example.demo.Service.Games.GameGenreMapService;
import com.example.demo.Service.Games.GameGenreService;
import com.example.demo.Service.IP.IpService;
import com.example.demo.Service.Posts.*;
import com.example.demo.Service.Redis.RedisPostService;
import com.example.demo.Service.UsersInfo.UserInfoService;
import com.example.demo.Util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


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
    @Autowired
    private PostCommentService postCommentService;
    @Autowired
    private RedisPostService redisPostService;
    @Autowired
    private PostImageService postImageService;

    @GetMapping("/all-games-genres/posts")
    public ResponseEntity ShowAllPostInOneGame(@RequestParam(value = "game") Short gameId, @RequestParam(value = "genre") Byte genreId,
                                               @RequestParam(value = "page") int page, @RequestParam(value = "size") int size) {
        ApiResponse apiResponse;
        GameGenreMapIdDTO gameGenreMapIdDTO = new GameGenreMapIdDTO();
        gameGenreMapIdDTO.setGameId(gameId);
        gameGenreMapIdDTO.setGenreId(genreId);
        page = page - 1;
        if (!GameIdValidator.CheckGameId(gameId) || !GenreIdValidator.CheckGenreId(genreId)) {
            apiResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "Game not found");
        } else if (gameGenreMapService.FindGamesGenresMapById(genreId, gameId) == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "Game not found");
        } else if (page < 0 || size <= 0) {
            apiResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "Invalid page or size");
        } else {
            Pageable pageable = PageRequest.of(page, size);
            List<PostInfoVO> showPostVOList = postInfoService.GetAllPostInfoInOneGame(gameGenreMapIdDTO);
            int startIdx = (int) pageable.getOffset();
            int endIdx = Math.min((startIdx + pageable.getPageSize()), showPostVOList.size());
            List<PostInfoVO> currentPageItems = showPostVOList.subList(startIdx, endIdx);
            Page<PostInfoVO> currentPage = new PageImpl<>(currentPageItems, pageable, showPostVOList.size());

            apiResponse = ApiResponse.success(currentPage);
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
        } else if (gameGenreMapService.FindGamesGenresMapById(genreId, gameId) == null) {
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
        } else if (gameGenreMapService.FindGamesGenresMapById(genreId, gameId) == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "Game not found");
        } else {
            GetPostDTO getPostDTO = new GetPostDTO();
            getPostDTO.setPostId(postId);
            getPostDTO.setGameId(gameId);
            getPostDTO.setGenreId(genreId);
            ShowPostVO showPostVO = postService.GetPost(getPostDTO);

            if (showPostVO == null) {
                apiResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "Post not found");
            } else {
                List<List<Object>> res = commentService.GetAllCommentsAndReplies(postId);
                apiResponse = ApiResponse.success(res);
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PostMapping(value = "/all-games-genres/edit-post", produces = {"application/json;charset=UTF-8", "text/html;charset=UTF-8"})
    public ResponseEntity SetPostInCache(HttpServletRequest request, @Validated @RequestBody PostDTO postDTO, HttpSession session) {
        ApiResponse apiResponse;
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
        } else if (!GameIdValidator.CheckGameId(postDTO.getGameId()) || !GenreIdValidator.CheckGenreId(postDTO.getGenreId())) {
            apiResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "Game not found");
        } else if (gameGenreMapService.FindGamesGenresMapById(postDTO.getGenreId(), postDTO.getGameId()) == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "Game not found");
        } else {
            logger.info("EditPost:::userId:::" + userId);
            String ipAddress = HttpUtils.getRequestIP(request);
            logger.info("EditPost:::ipAddress:::" + ipAddress);
            //set ip
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
            //set userid
            postDTO.setUserId(userId);
            postService.SetPostCache(postDTO);
            apiResponse = ApiResponse.success("Set Post Cache Successfully");
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PostMapping("/all-games-genres/save-post")
    public ResponseEntity SavePost(@RequestParam("imageFiles") List<MultipartFile> imageFiles, HttpSession session) throws IOException {
        ApiResponse apiResponse;
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
            return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
        } else {
            PostSavedVO postSavedVO = postService.SavePost(userId);
            if (postSavedVO != null) {
                //handle imageFiles??????
                apiResponse = ApiResponse.success(postSavedVO);
            } else {
                apiResponse = ApiResponse.error(ReturnCode.RC408.getCode(), "Request timeout, failed to save post, please try again");
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PostMapping("/all-games-genres/genre/home-merged")
    public ResponseEntity LatestPopularNewest(@Validated @RequestBody TypeDTO typeDTO) {
        ApiResponse apiResponse;
        logger.info("TypeDTO:::" + typeDTO.getType());
        switch (typeDTO.getType()) {
            case "latest":
                List<LatestPostVO> latestPosts = postGameMapService.ShowLatestPosts();
                apiResponse = ApiResponse.success(latestPosts);
                break;
            case "popular":
                List<PopularPostVO> popularPosts = postInfoService.GetMostPopularPosts();
                apiResponse = ApiResponse.success(popularPosts);
                break;
            case "newest":
                List<NewestCommentVO> newestCommentVOList = postCommentService.GetNewestComments();
                if (!newestCommentVOList.isEmpty()) {
                    apiResponse = ApiResponse.success(newestCommentVOList);
                } else {
                    apiResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "No Comment Found");
                }
                break;
            default:
                apiResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "Invalid type");
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

//    @GetMapping("/all-games-genres/genre/latest-posts")
//    public ResponseEntity ShowLatestPosts() {
//        ApiResponse apiResponse;
//        List<LatestPostVO> latestPosts = postGameMapService.ShowLatestPosts();
//        apiResponse = ApiResponse.success(latestPosts);
//        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
//    }
//
//    @GetMapping("/all-games-genres/genre/popular-posts")
//    public ResponseEntity ShowPopularPosts() {
//        ApiResponse apiResponse;
//        List<PopularPostVO> popularPosts = postInfoService.GetMostPopularPosts();
//        apiResponse = ApiResponse.success(popularPosts);
//        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
//    }
//    @GetMapping("/all-games-genres/genre/newest-comment")
//    public ResponseEntity ShowNewestComment() {
//        ApiResponse apiResponse;
//        List<NewestCommentVO> newestCommentVOList = postCommentService.GetNewestComments();
//        if (!newestCommentVOList.isEmpty()) {
//            apiResponse = ApiResponse.success(newestCommentVOList);
//        } else {
//            apiResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "No Comment Found");
//        }
//        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
//    }

}
