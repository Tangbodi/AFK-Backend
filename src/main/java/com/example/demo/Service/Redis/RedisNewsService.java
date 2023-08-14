package com.example.demo.Service.Redis;

import com.example.demo.Model.Entity.News;
import com.example.demo.Model.VO.NewsVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;

@Service
public class RedisNewsService {
    private static final Logger logger = LoggerFactory.getLogger(RedisNewsService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String NEWS_CACHE_KEY = "AFK_NEWS";

    public void UpdateNewsCache(List<NewsVO> newsVOList) throws JsonProcessingException {
        logger.info("Updating the News cache");
        Jedis jedis = new Jedis("localhost");
        try{
            logger.info("Deleting the old News cache");
            jedis.del(NEWS_CACHE_KEY);
            logger.info("Setting the new News cache");
            String news_json = objectMapper.writeValueAsString(newsVOList);
            jedis.set(NEWS_CACHE_KEY, news_json);
        }catch (JedisException e) {
            logger.error("Jedis Exception: " + e.getMessage());
        } finally {
            logger.info("Closing the jedis connection");
            jedis.close();
        }
    }
}
