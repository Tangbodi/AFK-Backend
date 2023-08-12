package com.example.demo.Controller;

import com.example.demo.Enum.ReturnCode;
import com.example.demo.Exception.UserNotFoundException;
import com.example.demo.Model.VO.GameGenreVO;
import com.example.demo.Model.VO.GameIconVO;
import com.example.demo.Model.VO.UserFavoriteGameVO;
import com.example.demo.Service.Games.GameGenreService;
import com.example.demo.Service.Games.GameIconService;
import com.example.demo.Service.Games.GameService;
import com.example.demo.Service.Redis.RedisGameIconService;
import com.example.demo.Service.UserFavoriteGame.UserFavoriteGameService;
import com.example.demo.Util.ApiResponse;
import com.example.demo.Util.GameIdValidator;
import com.example.demo.Util.GenreIdValidator;
import net.bytebuddy.asm.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class GamesController {
    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationController.class);
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

    @GetMapping("/all-games")
    public ResponseEntity GetAllGames() {
        ApiResponse apiResponse;
        if (redisGameIconService.CheckAllGameIconsCache()) {
            List<GameIconVO> gameIconVOList = redisGameIconService.GetAllGameIconsCache();
            apiResponse = ApiResponse.success(gameIconVOList);

        } else {
            List<GameIconVO> gameIconVOList = gameIconService.GetAllGameIcons();
            redisGameIconService.SetAllGameIconsCache(gameIconVOList);
            apiResponse = ApiResponse.success(gameIconVOList);
        }
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/all-games-genres")
    public ResponseEntity GetAllGameGenres() {
        List<GameGenreVO> gameGenreVOList = gameGenreService.GetAllGameGenres();
        ApiResponse apiResponse = ApiResponse.success(gameGenreVOList);
        return ResponseEntity.ok(apiResponse);
    }

//    @GetMapping("/all-games-genres/{genreId}")
//    public ResponseEntity GetGameUnderOneGenre(@PathVariable Byte genreId) {
//        ApiResponse apiResponse;
//        if (!GenreIdValidator.CheckGenreId(genreId)) {
//            apiResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "Genre not found");
//        } else {
//            List<Map<Short, Object>> gameIconUnderOneGenre = gameIconService.GetGameIconsUnderOneGenre(genreId);
//            apiResponse = ApiResponse.success(gameIconUnderOneGenre);
//        }
//        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
//    }

    @PostMapping("/all-games/{gameId}/save-forums")
    public ResponseEntity SaveForums(@PathVariable("gameId") Short gameId, HttpSession session) {
        ApiResponse apiResponse;
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC401.getCode(), "Please login to access this page");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        } else {
            if (!GameIdValidator.CheckGameId(gameId) || gameService.GetGameById(gameId) == null) {
                apiResponse = ApiResponse.error(ReturnCode.RC404.getCode(), "Game not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
            }
        }
        userFavoriteGameService.SetUserFavoriteGame(userId, gameId);
        apiResponse = ApiResponse.success(null);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/all-games/favorite-games")
    public ResponseEntity GetSavedForums(HttpSession session) {
        ApiResponse apiResponse;
        String userId = (String) session.getAttribute("userId");
        List<UserFavoriteGameVO> userFavoriteGameVOList;
        if (userId == null) {
            userFavoriteGameVOList = new ArrayList<>();
        } else {
            userFavoriteGameVOList = userFavoriteGameService.GetUserFavoriteGames(userId);
        }
        apiResponse = ApiResponse.success(userFavoriteGameVOList);
        return ResponseEntity.ok(apiResponse);
    }
}
