package com.example.demo.Controller;

import com.example.demo.Annotation.ValidGameId;
import com.example.demo.Annotation.ValidGenreId;
import com.example.demo.Annotation.ValidPostId;
import com.example.demo.Constant.Enum.ReturnCode;
import com.example.demo.Model.DTO.GameGenreMapIdDTO;
import com.example.demo.Model.DTO.GetPostDTO;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.DTO.TypeDTO;
import com.example.demo.Model.VO.*;
import com.example.demo.Service.Comments.CommentService;
import com.example.demo.Service.Games.GameGenreMapService;
import com.example.demo.Service.Games.GameIconService;
import com.example.demo.Service.IP.IpService;
import com.example.demo.Service.Posts.PostGameMapService;
import com.example.demo.Service.Posts.PostImageService;
import com.example.demo.Service.Posts.PostInfoService;
import com.example.demo.Service.Posts.PostService;
import com.example.demo.Service.UserLikeSave.UserLikeSaveService;
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
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@RestController
@Validated
@RequestMapping("/all-games-genres")
public class PostsController {
    private static final Logger logger = LoggerFactory.getLogger(PostsController.class);
    private static final int MAX_IMAGE_LENGTH = 9;
    @Autowired
    private PostService postService;
    @Autowired
    private IpService ipService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostGameMapService postGameMapService;
    @Autowired
    private PostInfoService postInfoService;
    @Autowired
    private GameGenreMapService gameGenreMapService;
    @Autowired
    private PostImageService postImageService;
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
            apiResponse = ApiResponse.success(Collections.emptyList());
        } else {
            List<PostInfoVO> showPostVOList = postInfoService.GetAllPostInfoInOneGame(gameGenreMapIdDTO);
            if (!showPostVOList.isEmpty()) {
                Pageable pageable = PageRequest.of(page, size);
                int startIdx = (int) pageable.getOffset();
                int endIdx = Math.min((startIdx + pageable.getPageSize()), showPostVOList.size());
                logger.info("startIdx:{}" + startIdx);
                logger.info("endIdx:{}" + endIdx);
                if (endIdx < startIdx) {
                    apiResponse = ApiResponse.success(Collections.emptyList());
                    return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
                }
                List<PostInfoVO> currentPageItems = showPostVOList.subList(startIdx, endIdx);
                Page<PostInfoVO> currentPage = new PageImpl<>(currentPageItems, pageable, showPostVOList.size());
                apiResponse = ApiResponse.success(currentPage);
            } else {
                apiResponse = ApiResponse.success(Collections.emptyList());
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
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (gameGenreMapService.FindGamesGenresMapById(genreId, gameId) == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Game not found");
        } else {
            GetPostDTO getPostDTO = new GetPostDTO();
            getPostDTO.setPostId(postId);
            getPostDTO.setGameId(gameId);
            getPostDTO.setGenreId(genreId);
            getPostDTO.setUserId(userId == null ? 0 : userId);
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
                                                    @RequestParam(value = "post") @ValidPostId Long postId,
                                                    @RequestParam(value = "page") int page,
                                                    @RequestParam(value = "size") int size, HttpServletRequest request) throws ParseException {
        ApiResponse apiResponse;
        page = page - 1;
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (gameGenreMapService.FindGamesGenresMapById(genreId, gameId) == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Game not found");
        } else if (page < 0 || size <= 0) {
            apiResponse = ApiResponse.success(Collections.emptyList());
        } else {
            //need pagination
            List<Map<String, Object>> res = commentService.GetAllCommentsAndReplies(postId, userId);
            if (!res.isEmpty()) {
                Pageable pageable = PageRequest.of(page, size);
                int startIdx = (int) pageable.getOffset();
                int endIdx = Math.min((startIdx + pageable.getPageSize()), res.size());
                logger.info("startIdx:{}" + startIdx);
                logger.info("endIdx:{}" + endIdx);
                if (endIdx < startIdx) {
                    apiResponse = ApiResponse.success(Collections.emptyList());
                    return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
                }
                List<Map<String, Object>> currentResItems = res.subList(startIdx, endIdx);
                Page<Map<String, Object>> currentResPage = new PageImpl<>(currentResItems, pageable, res.size());
                apiResponse = ApiResponse.success(currentResPage);
            } else {
                apiResponse = ApiResponse.success(Collections.emptyList());
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PostMapping(value = "/save-post")
    public ResponseEntity SavePost(HttpServletRequest request,
                                   @Validated @RequestBody PostDTO postDTO) {
        ApiResponse apiResponse;
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to share your game experience");
        } else if (gameGenreMapService.FindGamesGenresMapById(postDTO.getGenreId(), postDTO.getGameId()) == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Game not found");
        } else {
            logger.info("userId:{}" + userId);
            String ipAddress = HttpUtils.getRequestIP(request);
            logger.info("ipAddress:{}" + ipAddress);
            //set ip
            if (ipService.isValidInet4Address(ipAddress)) {
                logger.info("ipAddress is valid");
                String[] ip = ipAddress.split("\\.");
                logger.info("ipAddress split:{}" + ip);
                Long ipvF = (Long.valueOf(ip[0]) << 24) + (Long.valueOf(ip[1]) << 16) + (Long.valueOf(ip[2]) << 8) + Long.valueOf(ip[3]);
                logger.info("ipvF:{}" + ipvF);
                postDTO.setIpvFour(ipvF);
            } else if (ipService.isValidInet6Address(ipAddress)) {
                logger.info("ipAddress is valid");
                String[] ip = ipAddress.split(":");
                logger.info("ipvS:{}" + Arrays.toString(ip));
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
    public ResponseEntity SavePostImage(HttpServletRequest request, @RequestParam("images") MultipartFile[] images) {
        ApiResponse apiResponse;
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to share your images");
        } else if (!images[0].isEmpty() && images.length <= MAX_IMAGE_LENGTH) {
            try {
                List<String> postImageNameList = postImageService.SavePostImageToServer(images);
                if (!postImageNameList.isEmpty()) {
                    apiResponse = ApiResponse.success(postImageNameList);
                } else {
                    apiResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "Image is not an image or size is too large");
                }
            } catch (Exception e) {
                logger.error("Failed to save post image", e.getMessage(), e);
                apiResponse = ApiResponse.error(ReturnCode.RC500.getCode(), e.getMessage());
            }
        } else if (images[0].isEmpty()) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "No image found");
        } else {
            apiResponse = ApiResponse.error(ReturnCode.RC400.getCode(), "You can upload up to 9 images");
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PostMapping("/genre/latest-popular-newest")
    public ResponseEntity LatestPopularNewest(@Validated @RequestBody TypeDTO typeDTO) {
        ApiResponse apiResponse;
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
}
