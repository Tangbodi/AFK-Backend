package com.example.demo.Service.News;

import com.example.demo.Mapper.Repository.NewsRepository;
import com.example.demo.Model.DTO.NewsDTO;
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
                newsVO.setNewsId(String.valueOf(map.get("news_id")));
                newsVO.setGenreId((Byte) map.get("genre_id"));
                Short gameId = (Short) map.get("game_id");
                newsVO.setGameName(String.valueOf( map.get("game_name")));
                newsVO.setGameIconUrl(String.valueOf( map.get("icon_url")));
                newsVO.setGameId(gameId);
                newsVO.setSource(String.valueOf( map.get("source")));
                newsVO.setTitle(String.valueOf( map.get("title")));
                newsVO.setDescription(String.valueOf( map.get("description")));
                newsVO.setMediaContentUrl(String.valueOf( map.get("media_content_url")));
                newsVO.setContent(String.valueOf( map.get("content")));
                newsVO.setPubDate(String.valueOf(map.get("pub_date")));
                newsVOList.add(newsVO);
            }
            logger.info("Set all news from DB: {}", newsVOList.size());
            redisNewsService.SetAllGameNewsCache(newsVOList);
        } catch (Exception e) {
            logger.error("Error getting all news: {}", e.getMessage(), e);
        }
    }
    public void SetOneGameNewsList(Byte genreId, Short gameId){
        logger.info("Starting GetOneGameNews for gameId: {}", gameId);
        try{
            List<Map<String, Object>> newsList = newsRepository.findAllByGameId(gameId);
            logger.info("Got all news by gameId from DB: {}", newsList.size());
            List<NewsVO> newsVOList = new ArrayList<>();
            for(Map<String, Object> map : newsList) {
                NewsVO newsVO = new NewsVO();
                newsVO.setNewsId(String.valueOf(map.get("news_id")));
                newsVO.setGenreId(genreId);
                newsVO.setGameId(gameId);
                newsVO.setGameName(String.valueOf( map.get("game_name")));
                newsVO.setGameIconUrl(String.valueOf( map.get("icon_url")));
                newsVO.setSource(String.valueOf( map.get("source")));
                newsVO.setTitle(String.valueOf( map.get("title")));
                newsVO.setDescription(String.valueOf(map.get("description")));
                newsVO.setMediaContentUrl(String.valueOf( map.get("media_content_url")));
                newsVO.setContent(String.valueOf( map.get("content")));
                newsVO.setPubDate(String.valueOf(map.get("pub_date")));
                newsVOList.add(newsVO);
            }
            redisNewsService.SetOneGameNewsCache(gameId, newsVOList);
        } catch (Exception e) {
            logger.error("Error getting one game news: {}", e.getMessage(), e);
        }
    }
    public NewsVO GetOneGameNews(NewsDTO newsDTO){
        logger.info("Getting GetOneGameNews for gameId: {}", newsDTO.getGameId());
        try{
            List<Map<String, Object>> news = newsRepository.findByNewsId(newsDTO.getNewsId());
            if(news.isEmpty()){
                logger.info("News not found");
                return null;
            } else {
                NewsVO newsVO = new NewsVO();
                for(Map<String, Object> map : news) {
                    newsVO.setNewsId(String.valueOf( map.get("news_id")));
                    newsVO.setGenreId(newsDTO.getGenreId());
                    newsVO.setGameId(newsDTO.getGameId());
                    newsVO.setGameName(String.valueOf( map.get("game_name")));
                    newsVO.setGameIconUrl(String.valueOf( map.get("icon_url")));
                    newsVO.setSource(String.valueOf( map.get("source")));
                    newsVO.setTitle(String.valueOf( map.get("title")));
                    newsVO.setDescription(String.valueOf( map.get("description")));
                    newsVO.setMediaContentUrl(String.valueOf( map.get("media_content_url")));
                    newsVO.setContent(String.valueOf( map.get("content")));
                    newsVO.setPubDate(String.valueOf(map.get("pub_date")));
                }
                return newsVO;
            }
        } catch (Exception e) {
            logger.error("Error getting one game news: {}", e.getMessage(), e);
            return null;
        }
    }
}
