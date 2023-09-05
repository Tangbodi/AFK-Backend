package com.example.demo.Service.Redis;


import com.example.demo.Model.VO.NewsVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;
import java.util.List;

@Service
public class RedisNewsService {
    private static final Logger logger = LoggerFactory.getLogger(RedisNewsService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String NEWS_CACHE_KEY = "AFK_NEWS";
    @Autowired
    private JedisPool jedisPool;

    public void UpdateNewsCache(List<NewsVO> newsVOList) throws JsonProcessingException {
        logger.info("Updating the News cache");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            logger.info("Deleting the old News cache");
            jedis.del(NEWS_CACHE_KEY);
            logger.info("Setting the new News cache");
            String news_json = objectMapper.writeValueAsString(newsVOList);
            jedis.set(NEWS_CACHE_KEY, news_json);
        } catch (Exception e) {
            logger.error("Failed to update the News cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis)
                logger.info("Closing the jedis connection:::");
            jedis.close();
        }
    }
    public List<NewsVO> GetNewsCache(){
        logger.info("Getting the News cache");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String json = jedis.get(NEWS_CACHE_KEY);
            List<NewsVO> newsVOList = objectMapper.readValue(json, List.class);
            return newsVOList;
        } catch (Exception e) {
            logger.error("Failed to get the News cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis)
                logger.info("Closing the jedis connection:::");
            jedis.close();
        }
        return Collections.emptyList();
    }
}
