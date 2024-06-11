package com.example.demo.Service.Redis;

import com.example.demo.Model.Entity.GameIcon;
import com.example.demo.Model.VO.GameIconVO;
import com.example.demo.Model.VO.UserFavoriteGameVO;
import com.example.demo.Service.Games.GameIconService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class RedisGameIconService {
    private static final Logger logger = LoggerFactory.getLogger(RedisGameIconService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String ALL_GAME_ICON_KEY = "ALL_GAME_ICONS";
    @Autowired
    private JedisPool jedisPool;

    public void SetAllGameIconsCache(List<GameIconVO> gameIconVOList) throws JsonProcessingException {
        logger.info("Setting up all game icons cache: {}");
        Jedis jedis = new Jedis("localhost", 6379);
        jedis.auth("960c3dac4fa81b4204779fd16ad7c954f95942876b9c4fb1a255667a9dbe389d");
        try {
            jedis = jedisPool.getResource();
            String gameIconVOList_json = objectMapper.writeValueAsString(gameIconVOList);
            jedis.set(ALL_GAME_ICON_KEY, gameIconVOList_json);
        } catch (Exception e) {
            logger.error("Failed to set all game icons cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }

    public List<GameIconVO> GetAllGameIconsCache() {
        logger.info("Getting all game icons cache: {}");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String json = jedis.get(ALL_GAME_ICON_KEY);
            List<GameIconVO> res = objectMapper.readValue(json, new TypeReference<List<GameIconVO>>() {
            });
            return res;
        } catch (Exception e) {
            logger.error("Failed to get all game icons cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
        return Collections.emptyList();
    }
}
