package com.example.demo.Controller;

import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Model.VO.GameIconVO;
import com.example.demo.Model.VO.HomeGameImageVO;
import com.example.demo.Model.VO.UserFavoriteGameVO;
import com.example.demo.Service.Games.GameGenreMapService;
import com.example.demo.Service.Games.GameIconService;
import com.example.demo.Service.MQ.MQSender;
import com.example.demo.Service.Redis.RedisGameIconService;
import com.example.demo.Service.Redis.RedisService;
import com.example.demo.Service.Redis.RedisUserFavoriteGameService;
import com.example.demo.Service.UserFavoriteGame.UserFavoriteGameService;
import com.example.demo.Util.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/all-games")
public class GamesController {
    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationController.class);
    private static final String ALL_GAME_ICON_KEY = "ALL_GAME_ICONS";
    private static final String SAVED_GAME = ObjectNameEnum.SAVED_GAME_SET.getTypeName();
    @Autowired
    private GameIconService gameIconService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserFavoriteGameService userFavoriteGameService;
    @Autowired
    private RedisGameIconService redisGameIconService;
    @Autowired
    private RedisUserFavoriteGameService redisUserFavoriteGameService;


    @GetMapping("/")
    public ResponseEntity GetAllGameIcons() throws JsonProcessingException {
        ApiResponse apiResponse;
        List<GameIconVO> gameIconVOList;

        if (redisService.CacheExists(ALL_GAME_ICON_KEY)) {
            logger.info("ALL_GAME_ICONS exists in Redis cache");
            gameIconVOList = redisGameIconService.GetAllGameIconsCache();
        } else {
            logger.info("ALL_GAME_ICONS doesn't exist in Redis cache");
            gameIconVOList = gameIconService.GetAllGameIcons();
            redisGameIconService.SetAllGameIconsCache(gameIconVOList);
        }
        apiResponse = ApiResponse.success(gameIconVOList);

        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
    @GetMapping("/saved-games")
    public ResponseEntity GetSavedGames(HttpServletRequest request) throws IOException {
        ApiResponse apiResponse;
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId == null) {
            logger.info("User not logged in");
            apiResponse = ApiResponse.success(Collections.EMPTY_LIST);
        } else {
            //If user is logged in and user favorite games exists in Redis then get from Redis, else get from DB
            List<UserFavoriteGameVO> userFavoriteGameVOList;
            String key = SAVED_GAME + ":::" + userId;
            if(redisService.CacheExists(key)){
                logger.info("User favorite games exists in Redis cache");
                userFavoriteGameVOList = redisUserFavoriteGameService.GetUserFavoriteGameCache(key,userId);
            } else {
                logger.info("User favorite games doesn't exist in Redis cache");
                userFavoriteGameVOList = userFavoriteGameService.GetUserFavoriteGames(userId);
            }
            apiResponse = ApiResponse.success(userFavoriteGameVOList);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @GetMapping("/home-game-images")
    public ResponseEntity GetHomeGameImages() {
        ApiResponse apiResponse;
        List<HomeGameImageVO> homeGameImages = gameIconService.GetHomeGameImages();
        apiResponse = ApiResponse.success(homeGameImages);
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }


}
