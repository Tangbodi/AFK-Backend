package com.example.demo.Controller;

import com.example.demo.Annotation.ValidGameId;
import com.example.demo.Annotation.ValidGenreId;
import com.example.demo.Annotation.ValidPostId;
import com.example.demo.Enum.ReturnCode;
import com.example.demo.Model.DTO.*;
import com.example.demo.Model.VO.*;
import com.example.demo.Service.Comments.CommentService;
import com.example.demo.Service.Games.GameGenreMapService;
import com.example.demo.Service.Games.GameGenreService;
import com.example.demo.Service.Games.GameIconService;
import com.example.demo.Service.IP.IpService;
import com.example.demo.Service.Posts.PostGameMapService;
import com.example.demo.Service.Posts.PostImageService;
import com.example.demo.Service.Posts.PostInfoService;
import com.example.demo.Service.Posts.PostService;
import com.example.demo.Service.Redis.RedisPostService;
import com.example.demo.Service.UserFavoritePost.UserFavoritePostService;
import com.example.demo.Service.UsersInfo.UserInfoService;
import com.example.demo.Util.ApiResponse;
import com.example.demo.Util.HttpUtils;
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
import java.util.Arrays;
import java.util.List;


@RestController
@Validated
@RequestMapping("/all-games-genres")
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
    private PostGameMapService postGameMapService;
    @Autowired
    private PostInfoService postInfoService;
    @Autowired
    private GameGenreMapService gameGenreMapService;
    @Autowired
    private RedisPostService redisPostService;
    @Autowired
    private PostImageService postImageService;
    @Autowired
    private UserFavoritePostService userFavoritePostService;
    @Autowired
    private GameIconService gameIconService;

    @GetMapping("/posts")
    public ResponseEntity ShowAllPostsInOneGame(@RequestParam(value = "game") @ValidGameId Short gameId,
                                                @RequestParam(value = "genre") @ValidGenreId Byte genreId,
                                                @RequestParam(value = "page") int page,
                                                @RequestParam(value = "size") int size) {
        ApiResponse apiResponse;
        GameGenreMapIdDTO gameGenreMapIdDTO = new GameGenreMapIdDTO();
        gameGenreMapIdDTO.setGameId(gameId);
        gameGenreMapIdDTO.setGenreId(genreId);
        page = page - 1;
        if (gameGenreMapService.FindGamesGenresMapById(genreId, gameId) == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Game not found");
        } else if (page < 0 || size <= 0) {
            apiResponse = ApiResponse.success(null);
        } else {
            List<PostInfoVO> showPostVOList = postInfoService.GetAllPostInfoInOneGame(gameGenreMapIdDTO);
            if (!showPostVOList.isEmpty()) {
                Pageable pageable = PageRequest.of(page, size);
                int startIdx = (int) pageable.getOffset();
                int endIdx = Math.min((startIdx + pageable.getPageSize()), showPostVOList.size());
                List<PostInfoVO> currentPageItems = showPostVOList.subList(startIdx, endIdx);
                Page<PostInfoVO> currentPage = new PageImpl<>(currentPageItems, pageable, showPostVOList.size());
                apiResponse = ApiResponse.success(currentPage);
            } else {
                apiResponse = ApiResponse.success(showPostVOList);
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @GetMapping("/game-info")
    public ResponseEntity GetOneGameIconInfo(@RequestParam(value = "game") @ValidGameId Short gameId,
                                             @RequestParam(value = "genre") @ValidGenreId Byte genreId) {
        ApiResponse apiResponse;
        if (gameGenreMapService.FindGamesGenresMapById(genreId, gameId) == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Game not found");
        } else {
            GameGenreMapIdDTO gameGenreMapIdDTO = new GameGenreMapIdDTO();
            gameGenreMapIdDTO.setGameId(gameId);
            gameGenreMapIdDTO.setGenreId(genreId);
            GameIconVO gameIconVO = gameIconService.GetOneGameIcon(gameGenreMapIdDTO);
            apiResponse = ApiResponse.success(gameIconVO);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @GetMapping("/post-body")
    public ResponseEntity ShowPostBody(HttpServletRequest request,
                                       @RequestParam(value = "game") @ValidGameId Short gameId,
                                       @RequestParam(value = "genre") @ValidGenreId Byte genreId,
                                       @RequestParam(value = "post") @ValidPostId Long postId) {
        ApiResponse apiResponse;
        GameGenreMapIdDTO gameGenreMapIdDTO = new GameGenreMapIdDTO();
        gameGenreMapIdDTO.setGameId(gameId);
        gameGenreMapIdDTO.setGenreId(genreId);
        if (gameGenreMapService.FindGamesGenresMapById(genreId, gameId) == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Game not found");
        } else {
            GetPostDTO getPostDTO = new GetPostDTO();
            getPostDTO.setPostId(postId);
            getPostDTO.setGameId(gameId);
            getPostDTO.setGenreId(genreId);
            ShowPostBodyVO showPostBodyVO = postService.GetPost(getPostDTO);
            if (showPostBodyVO == null) {
                apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Post not found");
            } else {
                apiResponse = ApiResponse.success(showPostBodyVO);
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @GetMapping("/comments-replies")
    public ResponseEntity ShowAllCommentsAndReplies(@RequestParam(value = "game") @ValidGameId Short gameId,
                                                    @RequestParam(value = "genre") @ValidGenreId Byte genreId,
                                                    @RequestParam(value = "post") @ValidPostId Long postId) {
        ApiResponse apiResponse;
        GameGenreMapIdDTO gameGenreMapIdDTO = new GameGenreMapIdDTO();
        gameGenreMapIdDTO.setGameId(gameId);
        gameGenreMapIdDTO.setGenreId(genreId);
        if (gameGenreMapService.FindGamesGenresMapById(genreId, gameId) == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Game not found");
        } else {
            GetPostDTO getPostDTO = new GetPostDTO();
            getPostDTO.setPostId(postId);
            getPostDTO.setGameId(gameId);
            getPostDTO.setGenreId(genreId);
            ShowPostBodyVO showPostBodyVO = postService.GetPost(getPostDTO);
            if (showPostBodyVO == null) {
                apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Post not found");
            } else {
                //need pagination
                List<List<Object>> res = commentService.GetAllCommentsAndReplies(postId);
                apiResponse = ApiResponse.success(res);
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PostMapping(value = "/save-post")
    public ResponseEntity SavePost(HttpServletRequest request,
                                   @RequestBody PostDTO postDTO, HttpSession session) {
        ApiResponse apiResponse;
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to share your game experience");
        } else if (gameGenreMapService.FindGamesGenresMapById(postDTO.getGenreId(), postDTO.getGameId()) == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Game not found");
        } else {
            logger.info("userId:::" + userId);
            String ipAddress = HttpUtils.getRequestIP(request);
            logger.info("ipAddress:::" + ipAddress);
            //set ip
            if (ipService.isValidInet4Address(ipAddress)) {
                logger.info("ipAddress is valid");
                String[] ip = ipAddress.split("\\.");
                logger.info("ipAddress split:::" + ip);
                Long ipvF = (Long.valueOf(ip[0]) << 24) + (Long.valueOf(ip[1]) << 16) + (Long.valueOf(ip[2]) << 8) + Long.valueOf(ip[3]);
                logger.info("ipvF:::" + ipvF);
                postDTO.setIpvFour(ipvF);
            } else if (ipService.isValidInet6Address(ipAddress)) {
                logger.info("ipAddress is valid");
                String[] ip = ipAddress.split(":");
                logger.info("ipvS:::" + Arrays.toString(ip));
                postDTO.setIpvSix(ip.toString());
            } else {
                apiResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "Invalid IP Address");
                return ResponseEntity.badRequest().body(apiResponse);
            }
            //set userid
            postDTO.setUserId(userId);
            try {
                PostSavedVO postSavedVO = postService.SavePost(postDTO);
                apiResponse = ApiResponse.success(postSavedVO);
            } catch (Exception e) {
                logger.error("Failed to save post", e.getMessage(), e);
                apiResponse = ApiResponse.error(ReturnCode.RC500.getCode(), e.getMessage());
            }

        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PostMapping(value = "/save-post-image")
    public ResponseEntity SavePostImage(HttpServletRequest request, @RequestParam("images") List<MultipartFile> images, HttpSession session) {
        ApiResponse apiResponse;
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to share your images");
        } else if (!images.isEmpty() && images.size() <= 9) {
            try {
                List<String> postImageNameList = postImageService.SavePostImageToServer(images);
                apiResponse = ApiResponse.success(postImageNameList);
            } catch (Exception e) {
                logger.error("Failed to save post image", e.getMessage(), e);
                apiResponse = ApiResponse.error(ReturnCode.RC500.getCode(), e.getMessage());
            }
        } else {
            apiResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "You can upload a maximum of 3 images");

        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

//    @PostMapping("/save-post")
//    public ResponseEntity SavePost(@RequestBody PostDTO postDTO, HttpSession session) throws IOException {
//        ApiResponse apiResponse;
//        Long userId = (Long) session.getAttribute("userId");
//        if (userId == null) {
//            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Sign in to continue to save post");
//            return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
//        } else {
//            PostSavedVO postSavedVO = postService.SavePost(postDTO);
//            if (postSavedVO != null) {
//                apiResponse = ApiResponse.success(postSavedVO);
//            } else {
//                apiResponse = ApiResponse.error(ReturnCode.RC408.getCode(), "Request timeout, failed to save post, please try again");
//            }
//        }
//        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
//    }

    @PostMapping("/genre/latest-popular-newest")
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
                List<NewestCommentVO> newestCommentVOList = commentService.GetNewestComments();
                apiResponse = ApiResponse.success(newestCommentVOList);
                break;
            default:
                apiResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "Invalid type");
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PostMapping("/genre/like-save-post")
    public ResponseEntity SetUserLikeSavePost(@Validated @RequestBody UserLikesSavesPostDTO userLikesSavesPostDTO, HttpSession session) {
        ApiResponse apiResponse;
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Sign in to make your opinion count");
            return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
        } else {
            userLikesSavesPostDTO.setUserId(userId);
            boolean status;
            switch (userLikesSavesPostDTO.getType()) {
                case "like":
                    status = userFavoritePostService.SetUserLikePost(userLikesSavesPostDTO);
                    apiResponse = ApiResponse.success(status);
                    break;
                case "save":
                    status = userFavoritePostService.SetUserSavePost(userLikesSavesPostDTO);
                    apiResponse = ApiResponse.success(status);
                    break;
                default:
                    apiResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "Invalid type");
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}
