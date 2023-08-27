package com.example.demo.Controller;

import com.example.demo.Model.VO.NewsVO;
import com.example.demo.Service.News.NewsService;
import com.example.demo.Service.Redis.RedisNewsService;
import com.example.demo.Service.Redis.RedisService;
import com.example.demo.Util.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);
    private static final String NEWS_CACHE_KEY = "AFK_NEWS";
    @Autowired
    private NewsService newsService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisNewsService redisNewsService;

    @GetMapping("/get-news")
    public ResponseEntity GetNews() {
        ApiResponse apiResponse;
        List<NewsVO> newsVOList;
        if (redisService.CacheExists(NEWS_CACHE_KEY)) {
            newsVOList = redisNewsService.GetNewsCache();
        } else {
            newsService.ProxyXML();
            newsVOList = newsService.GetNews();
        }
        apiResponse = ApiResponse.success(newsVOList);
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}
