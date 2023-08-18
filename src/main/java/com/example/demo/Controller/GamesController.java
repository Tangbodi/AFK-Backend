package com.example.demo.Controller;

import com.example.demo.Enum.ReturnCode;
import com.example.demo.Model.DTO.GameGenreMapIdDTO;
import com.example.demo.Model.VO.GameGenreVO;
import com.example.demo.Model.VO.GameIconVO;
import com.example.demo.Model.VO.HomeGameImageVO;
import com.example.demo.Model.VO.UserFavoriteGameVO;
import com.example.demo.Service.Games.GameGenreMapService;
import com.example.demo.Service.Games.GameGenreService;
import com.example.demo.Service.Games.GameIconService;
import com.example.demo.Service.Games.GameService;
import com.example.demo.Service.Redis.RedisGameIconService;
import com.example.demo.Service.UserFavoriteGame.UserFavoriteGameService;
import com.example.demo.Util.ApiResponse;
import com.example.demo.Util.GameIdValidator;
import com.example.demo.Util.GenreIdValidator;
import com.example.demo.Util.PostIdValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
public class GamesController {
    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationController.class);
    private static final String ALL_GAME_ICON_KEY = "ALL_GAME_ICONS";

    @Autowired
    private GameIconService gameIconService;
    @Autowired
    private RedisGameIconService redisGameIconService;
    @Autowired
    private GameGenreService gameGenreService;
    @Autowired
    private UserFavoriteGameService userFavoriteGameService;
    @Autowired
    private GameService gameService;
    @Autowired
    private GameGenreMapService gameGenreMapService;


    @GetMapping("/all-games")
    public ResponseEntity GetAllGames() throws JsonProcessingException {
        ApiResponse apiResponse;
        List<GameIconVO> gameIconVOList;
        Jedis jedis = new Jedis("localhost");
        boolean existsInCache = jedis.exists(ALL_GAME_ICON_KEY);
        if (existsInCache) {
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

    @GetMapping("/all-games-genres")
    public ResponseEntity GetAllGameGenres() {
        List<GameGenreVO> gameGenreVOList = gameGenreService.GetAllGameGenres();
        ApiResponse apiResponse = ApiResponse.success(gameGenreVOList);
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PostMapping("/all-games/save-favorite-game")
    public ResponseEntity SaveFavoriteGames(@Validated @RequestBody GameGenreMapIdDTO gameGenreMapIdDTO, HttpSession session) {
        ApiResponse apiResponse;
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Sign in to access games that you’ve liked or saved");
        } else if (!GameIdValidator.CheckGameId(gameGenreMapIdDTO.getGameId()) || !GenreIdValidator.CheckGenreId(gameGenreMapIdDTO.getGenreId())) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Game not found");
        } else if (gameGenreMapService.FindGamesGenresMapById(gameGenreMapIdDTO.getGenreId(),gameGenreMapIdDTO.getGameId()) == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Game not found");
        }
        else {
            gameGenreMapIdDTO.setUserId(userId);
            boolean saveStatus = userFavoriteGameService.SetUserFavoriteGame(gameGenreMapIdDTO);
            apiResponse = ApiResponse.success(saveStatus);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @GetMapping("/all-games/favorite-games")
    public ResponseEntity GetFavoriteGames(HttpSession session) {
        ApiResponse apiResponse;
        String userId = (String) session.getAttribute("userId");
        List<UserFavoriteGameVO> userFavoriteGameVOList;
        if (userId == null) {
            logger.info("User not logged in");
            userFavoriteGameVOList = new ArrayList<>();
        } else {
            userFavoriteGameVOList = userFavoriteGameService.GetUserFavoriteGames(userId);
        }
        apiResponse = ApiResponse.success(userFavoriteGameVOList);
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
    @GetMapping("/all-games/home-game-images")
    public ResponseEntity GetHomeGameImages() {
        ApiResponse apiResponse;
        List<HomeGameImageVO> homeGameImages = gameIconService.GetHomeGameImages();
        apiResponse = ApiResponse.success(homeGameImages);
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}
