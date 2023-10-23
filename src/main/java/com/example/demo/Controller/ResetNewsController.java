package com.example.demo.Controller;

import com.example.demo.Service.News.GameRantNewsService;
import com.example.demo.Service.News.NewsService;
import com.example.demo.Service.News.SteamNewsService;
import com.example.demo.Service.Redis.RedisNewsService;
import com.example.demo.Util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class ResetNewsController {
    private static final List<Integer> STEAM_SET = Arrays.asList(102, 123, 126, 129, 138, 201, 204, 225, 243, 405, 417, 423, 426, 315, 324, 231, 234, 207, 402, 249, 420, 507, 603, 609, 612, 618, 621, 624, 630, 135, 117);
    private static final List<Integer> GAME_RANT_SET = Arrays.asList(447, 237, 111, 114, 210, 240, 516, 600);
    @Autowired
    private RedisNewsService redisNewsService;
    @Autowired
    private SteamNewsService steamNewsService;
    @Autowired
    private GameRantNewsService gameRantNewsService;
    @Autowired
    private NewsService newsService;
    @PostMapping("/reset-game-news")
    public ResponseEntity SetAllGameNewsCache() {
        ApiResponse apiResponse;
        redisNewsService.DeleteAllGameNewsCache();
        redisNewsService.DeleteOneGameNewsListCache();
        for (Integer gameId : STEAM_SET) {
            steamNewsService.ProxyXML(gameId);
        }
        //Delete all news by source = GameRant for avoiding duplicate news
        gameRantNewsService.DeleteNews();
        for (Integer gameId : GAME_RANT_SET) {
            gameRantNewsService.ProxyXML(gameId);
        }
        newsService.SetAllNews();
        apiResponse = ApiResponse.success("Set all game news cache successfully");
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}
