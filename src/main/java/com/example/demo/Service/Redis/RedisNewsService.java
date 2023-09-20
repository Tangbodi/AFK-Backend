package com.example.demo.Service.Redis;


import com.example.demo.Model.VO.NewsVO;
import com.fasterxml.jackson.core.type.TypeReference;
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
    private static final String AFK_GAME_NEWS = "AFK_GAME_NEWS:";
    private static final String ALL_AFK_GAME_NEWS = "ALL_AFK_GAME_NEWS";


    @Autowired
    private JedisPool jedisPool;

    public void SetAllGameNewsCache(List<NewsVO> newsVOList) {
        logger.info("Setting up all game news cache: {}");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String newsVOList_json = objectMapper.writeValueAsString(newsVOList);
            jedis.set(ALL_AFK_GAME_NEWS, newsVOList_json);
        } catch (Exception e) {
            logger.error("Failed to set all game icons cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }

    public void SetOneGameNewsCache(Short gameId, List<NewsVO> newsVOList) {
        logger.info("Setting up one game news cache: {}", gameId);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String newsVOList_json = objectMapper.writeValueAsString(newsVOList);
            jedis.set(AFK_GAME_NEWS + gameId, newsVOList_json);
        } catch (Exception e) {
            logger.error("Failed to set one game news cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }


    public List<NewsVO> GetAllGameNewsCache() {
        logger.info("Getting all game news cache: {}");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String json = jedis.get(ALL_AFK_GAME_NEWS);
            List<NewsVO> res = objectMapper.readValue(json, new TypeReference<List<NewsVO>>() {
            });
            return res;
        } catch (Exception e) {
            logger.error("Failed to get all game news cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
        return Collections.emptyList();
    }

    public List<NewsVO> GetOneGameNewsListCache(Short gameId) {
        logger.info("Getting game news cache: {}", gameId);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String json = jedis.get(AFK_GAME_NEWS + gameId);
            List<NewsVO> newsVOList = objectMapper.readValue(json, List.class);
            return newsVOList;
        } catch (Exception e) {
            logger.error("Failed to get game news cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
        return null; // Handle cache miss or any other errors
    }
//    public String GetEmailByToken(String token) {
//        logger.info("Getting email by token: {}" + token);
//        Jedis jedis = null;
//        try {
//            jedis = jedisPool.getResource();
//            return jedis.get(EMAIL_VALIDATION + token);
//        } catch (Exception e) {
//            logger.error("Failed to get email by token", e.getMessage(), e);
//            // Handle exceptions
//        } finally {
//            if (null != jedis) {
//                logger.info("Closing the jedis connection:::");
//                jedis.close();
//            }
//        }
//        return "";
//    }

    public void DeleteAllGameNewsCache() {
        logger.info("Updating all game news cache");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(ALL_AFK_GAME_NEWS);
        } catch (Exception e) {
            logger.error("Failed to update all game news cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }
}
