package com.example.demo.Controller;

import com.example.demo.Model.VO.NewsVO;
import com.example.demo.Service.News.NewsService;
import com.example.demo.Util.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.util.List;

@RestController
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);
    private static final String NEWS_CACHE_KEY = "AFK_NEWS";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private NewsService newsService;

    @GetMapping("/get-news")
    public ResponseEntity GetNews() throws JsonProcessingException {
        ApiResponse apiResponse;
        Jedis jedis = new Jedis("localhost");
        boolean existsInCache = jedis.exists(NEWS_CACHE_KEY);
        List<NewsVO> newsVOList;
        if (existsInCache) {
            logger.info("NEWS_CACHE exists in Redis cache");
            logger.info("Get NEWS from Redis cache");
            String json = jedis.get(NEWS_CACHE_KEY);
            newsVOList = objectMapper.readValue(json, List.class);
        } else {
            logger.info("NEWS_CACHE doesn't exist in Redis cache");
            logger.info("Catching News from rssFeedURL");
            newsService.ProxyXML();
            logger.info("Get NEWS from MySQL");
            newsVOList = newsService.GetNews();
        }
        apiResponse = ApiResponse.success(newsVOList);
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}
