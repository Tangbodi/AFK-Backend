package com.example.demo.Controller;

import com.example.demo.Model.VO.GameGenreVO;
import com.example.demo.Model.VO.GameIconVO;
import com.example.demo.Service.Games.GameGenreService;
import com.example.demo.Service.Games.GameIconService;
import com.example.demo.Service.Redis.RedisGameIconService;
import com.example.demo.Util.ApiResponse;
import com.example.demo.Util.GenreValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/all-games")
    public ResponseEntity GetAllGame() {
        if (redisGameIconService.CheckAllGameIconsCache()) {
            List<GameIconVO> gameIconVOList = redisGameIconService.GetAllGameIconsCache();
            ApiResponse<List<GameIconVO>> apiResponse = ApiResponse.success(gameIconVOList);
            return ResponseEntity.ok(apiResponse);
        } else {
            List<GameIconVO> gameIconVOList = gameIconService.GetAllGameIcon();
            redisGameIconService.SetAllGameIconsCache(gameIconVOList);
            ApiResponse<List<GameIconVO>> apiResponse = ApiResponse.success(gameIconVOList);
            return ResponseEntity.ok(apiResponse);
        }
    }

    @GetMapping("/all-game-genres")
    public ResponseEntity GetAllGameGenres() {
        List<GameGenreVO> gameGenreVOList = gameGenreService.GetAllGameGenres();
        ApiResponse apiResponse = ApiResponse.success(gameGenreVOList);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/game-genre/{genreId}")
    public ResponseEntity GetGameUnderOneGenre(@PathVariable Byte genreId) {
        if(!GenreValidator.CheckGenreId(genreId)){
            ApiResponse errorResponse = ApiResponse.error(404, "Genre not found");
            return ResponseEntity.status(404).body(errorResponse);
        } else {
            List<Map<Short, Object>> gameIconUnderOneGenre = gameIconService.GetGameIconUnderOneGenre(genreId);
            ApiResponse apiResponse = ApiResponse.success(gameIconUnderOneGenre);
            return ResponseEntity.ok(apiResponse);
        }
    }
}
