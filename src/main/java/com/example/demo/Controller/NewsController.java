package com.example.demo.Controller;

import com.example.demo.Annotation.ValidGameId;
import com.example.demo.Annotation.ValidGenreId;
import com.example.demo.Constant.Enum.ReturnCode;
import com.example.demo.Model.DTO.NewsDTO;
import com.example.demo.Model.VO.NewsVO;
import com.example.demo.Model.VO.PostInfoVO;
import com.example.demo.Service.Games.GameGenreMapService;
import com.example.demo.Service.News.GameRantNewsService;
import com.example.demo.Service.News.NewsService;
import com.example.demo.Service.News.SteamNewsService;
import com.example.demo.Service.Redis.RedisNewsService;
import com.example.demo.Service.Redis.RedisService;
import com.example.demo.Util.ApiResponse;
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

import java.io.IOException;
import java.util.*;

@RestController
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);
    private static final String AFK_GAME_NEWS = "AFK_GAME_NEWS:";
    private static final String ALL_AFK_GAME_NEWS = "ALL_AFK_GAME_NEWS";
    private static final List<Integer> STEAM_SET = Arrays.asList(102, 123, 126, 129, 138, 201, 204, 225, 243, 405, 417, 423, 426, 315, 324, 231, 234, 207, 402, 249, 420, 507, 603, 609, 612, 618, 621, 624, 630, 135, 117);
    private static final List<Integer> GAME_RANT_SET = Arrays.asList(447, 237, 111, 114, 210, 240, 516, 600);

    @Autowired
    private NewsService newsService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisNewsService redisNewsService;
    @Autowired
    private SteamNewsService steamNewsService;
    @Autowired
    private GameRantNewsService gameRantNewsService;
    @Autowired
    private GameGenreMapService gameGenreMapService;

    @PostMapping("/set-game-news")
    public ResponseEntity SetAllGameNewsCache() {
        ApiResponse apiResponse;
        redisNewsService.DeleteAllGameNewsCache();
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

    @GetMapping("/all-game-news")
    public ResponseEntity GetAllGameNewsCache( @RequestParam(value = "page") int page,
                                               @RequestParam(value = "size") int size) {
        ApiResponse apiResponse;
        page = page - 1;
        if(page < 0 || size <= 0){
            apiResponse = ApiResponse.success(Collections.emptyList());
        } else if (redisService.CacheExists(ALL_AFK_GAME_NEWS)) {
            logger.info("Getting all game news from Redis");
            List<NewsVO> newsVOList = redisNewsService.GetAllGameNewsCache();
            Pageable pageable = PageRequest.of(page, size);
            int startIdx = (int) pageable.getOffset();
            int endIdx = Math.min((startIdx + pageable.getPageSize()), newsVOList.size());
            if (endIdx < startIdx) {
                apiResponse = ApiResponse.success(Collections.emptyList());
                return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
            } else {
                List<NewsVO> currentPageItems = newsVOList.subList(startIdx, endIdx);
                Page<NewsVO> currentPage = new PageImpl<>(currentPageItems, pageable, newsVOList.size());
                apiResponse = ApiResponse.success(currentPage);
            }
        } else {
            logger.info("Getting all game news from DB");
            newsService.SetAllNews();
            List<NewsVO> newsVOList = redisNewsService.GetAllGameNewsCache();
            Pageable pageable = PageRequest.of(page, size);
            int startIdx = (int) pageable.getOffset();
            int endIdx = Math.min((startIdx + pageable.getPageSize()), newsVOList.size());
            if (endIdx < startIdx) {
                apiResponse = ApiResponse.success(Collections.emptyList());
                return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
            } else {
                List<NewsVO> currentPageItems = newsVOList.subList(startIdx, endIdx);
                Page<NewsVO> currentPage = new PageImpl<>(currentPageItems, pageable, newsVOList.size());
                apiResponse = ApiResponse.success(currentPage);
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @GetMapping("/game-news-list")
    public ResponseEntity GetOneGameNewsCache(@RequestParam(value = "game") @ValidGameId Short gameId,
                                              @RequestParam(value = "genre") @ValidGenreId Byte genreId,
                                              @RequestParam(value = "page") int page,
                                              @RequestParam(value = "size") int size) throws IOException {
        ApiResponse apiResponse;
        page = page - 1;
        if (gameGenreMapService.FindGamesGenresMapById(genreId, gameId) == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Game not found");
        } else if(page < 0 || size <= 0){
            apiResponse = ApiResponse.success(Collections.emptyList());
        } else {
            if(redisService.CacheExists(AFK_GAME_NEWS+gameId)){
                logger.info("Getting one game news from Redis");
                List<NewsVO> newsVOList = redisNewsService.GetOneGameNewsListCache(gameId);
                Pageable pageable = PageRequest.of(page, size);
                int startIdx = (int) pageable.getOffset();
                int endIdx = Math.min((startIdx + pageable.getPageSize()), newsVOList.size());
                if (endIdx < startIdx) {
                    apiResponse = ApiResponse.success(Collections.emptyList());
                    return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
                } else {
                    List<NewsVO> currentPageItems = newsVOList.subList(startIdx, endIdx);
                    Page<NewsVO> currentPage = new PageImpl<>(currentPageItems, pageable, newsVOList.size());
                    apiResponse = ApiResponse.success(currentPage);
                }
            } else {
                logger.info("Getting one game news from DB");
                newsService.SetOneGameNewsList(genreId, gameId);
                List<NewsVO> newsVOList = redisNewsService.GetOneGameNewsListCache(gameId);
                Pageable pageable = PageRequest.of(page, size);
                int startIdx = (int) pageable.getOffset();
                int endIdx = Math.min((startIdx + pageable.getPageSize()), newsVOList.size());
                if (endIdx < startIdx) {
                    apiResponse = ApiResponse.success(Collections.emptyList());
                    return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
                } else {
                    List<NewsVO> currentPageItems = newsVOList.subList(startIdx, endIdx);
                    Page<NewsVO> currentPage = new PageImpl<>(currentPageItems, pageable, newsVOList.size());
                    apiResponse = ApiResponse.success(currentPage);
                }
            }
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
    @GetMapping("/game-news")
    public ResponseEntity GetOneGameNewsCache(@RequestParam(value = "game") @ValidGameId Short gameId,
                                              @RequestParam(value = "genre") @ValidGenreId Byte genreId,
                                              @RequestParam(value = "newsId") String newsId) throws IOException {
        ApiResponse apiResponse;
        if (gameGenreMapService.FindGamesGenresMapById(genreId, gameId) == null) {
            apiResponse = ApiResponse.error(ReturnCode.RC200.getCode(), "Game not found");
        } else {
            NewsDTO newsDTO = new NewsDTO();
            newsDTO.setNewsId(newsId);
            newsDTO.setGameId(gameId);
            newsDTO.setGenreId(genreId);
            NewsVO newsVO = newsService.GetOneGameNews(newsDTO);
            apiResponse = ApiResponse.success(newsVO);
        }
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}
