package com.example.demo.Service.News;

import com.example.demo.Mapper.Repository.NewsRepository;
import com.example.demo.Model.Entity.News;
import com.example.demo.Model.VO.NewsVO;
import com.example.demo.Service.Redis.RedisNewsService;
import com.example.demo.Service.Redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NewsService {
    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private RedisNewsService redisNewsService;
    @Autowired
    private RedisService redisService;
    public void SetAllNews() {
        logger.info("Setting GetAllNews:::");
        try {
            List<NewsVO> newsVOList = new ArrayList<>();
            List<Map<String, Object>> newsList = newsRepository.findAllNewsByPublishDate();
            for (Map<String, Object> map : newsList) {
                NewsVO newsVO = new NewsVO();
                newsVO.setNewsId((String) map.get("news_id"));
                newsVO.setGenreId((Byte) map.get("genre_id"));
                Short gameId = (Short) map.get("game_id");
                newsVO.setGameName((String) map.get("game_name"));
                newsVO.setGameIconUrl((String) map.get("icon_url"));
                newsVO.setGameId(gameId);
                newsVO.setSource((String) map.get("source"));
                newsVO.setTitle((String) map.get("title"));
                newsVO.setDescription((String) map.get("description"));
                newsVO.setMediaContentUrl((String) map.get("media_content_url"));
                newsVO.setContent((String) map.get("content"));
                newsVO.setPubDate((String)map.get("pub_date"));
                newsVOList.add(newsVO);
            }
            logger.info("Set all news from DB: {}", newsVOList.size());
            redisNewsService.SetAllGameNewsCache(newsVOList);
        } catch (Exception e) {
            logger.error("Error getting all news: {}", e.getMessage(), e);
        }
    }
    public void SetOneGameNews(Byte genreId, Short gameId){
        logger.info("Starting GetOneGameNews for gameId: {}", gameId);
        try{
            List<Map<String, Object>> newsList = newsRepository.findAllByGameId(gameId);
            logger.info("Got all news by gameId from DB: {}", newsList.size());
            List<NewsVO> newsVOList = new ArrayList<>();
            for(Map<String, Object> map : newsList) {
                NewsVO newsVO = new NewsVO();
                newsVO.setNewsId((String) map.get("news_id"));
                newsVO.setGenreId(genreId);
                newsVO.setGameId(gameId);
                newsVO.setGameName((String) map.get("game_name"));
                newsVO.setGameIconUrl((String) map.get("icon_url"));
                newsVO.setSource((String) map.get("source"));
                newsVO.setTitle((String) map.get("title"));
                newsVO.setDescription((String) map.get("description"));
                newsVO.setMediaContentUrl((String) map.get("media_content_url"));
                newsVO.setContent((String) map.get("content"));
                newsVO.setPubDate((String)map.get("pub_date"));
                newsVOList.add(newsVO);
            }
            redisNewsService.SetOneGameNewsCache(gameId, newsVOList);
        } catch (Exception e) {
            logger.error("Error getting one game news: {}", e.getMessage(), e);
        }
    }
}
