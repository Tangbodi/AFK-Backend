package com.example.demo.Controller;

import com.example.demo.Enum.ReturnCode;
import com.example.demo.Mapper.Repository.GameGenresRepository;
import com.example.demo.Model.DTO.PostDTO;
import com.example.demo.Model.VO.PostVO;
import com.example.demo.Model.VO.UserInfoVO;
import com.example.demo.Service.Games.GameGenreService;
import com.example.demo.Service.IP.IpService;
import com.example.demo.Service.Posts.PostService;
import com.example.demo.Service.UsersInfo.UsersInfoService;
import com.example.demo.Util.ApiResponse;
import com.example.demo.Util.GenreValidator;
import com.example.demo.Util.HttpUtils;
import com.example.demo.Util.UserIdValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;


@RestController
@Validated
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    @Autowired
    private PostService postService;
    @Autowired
    private IpService ipService;
    @Autowired
    private UsersInfoService usersInfoService;
    @Autowired
    private GameGenreService gameGenreService;

    @PostMapping(value = "user/{userId}/edit-post/{genreId}",produces = {"application/json;charset=UTF-8", "text/html;charset=UTF-8"})
    public ResponseEntity EditPost(HttpServletRequest request, @PathVariable("userId") String userId, @PathVariable("genreId") Byte genreId, @Validated @RequestBody PostDTO postDTO) throws IOException {
        if(!GenreValidator.CheckGenreId(genreId) || !gameGenreService.isGameGenreExist(genreId)) {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "Genre Not Found");
            return ResponseEntity.badRequest().body(errorResponse);
        } else if(!UserIdValidator.CheckUserId(userId) || usersInfoService.GetUserInfo(userId)==null) {
            ApiResponse errorResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "User Not Found");
            return ResponseEntity.badRequest().body(errorResponse);
        } else{
            //do nothing
        }
        logger.info("EditPost:::userId:::" + userId + ":::genreId:::" + genreId);
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
        UserInfoVO userInfoVO = usersInfoService.GetUserInfo(userId);
        postDTO.setUserId(userId);
        postDTO.setGenreId(genreId);
        postDTO.setUsername(userInfoVO.getUsername());
        postDTO.setCreatedAt(Instant.now());
        PostVO postVO = postService.EditPost(postDTO);
        ApiResponse apiResponse = ApiResponse.success(postVO);
        return ResponseEntity.ok(apiResponse);
    }
}
