package com.example.demo.Service.Redis;

import com.example.demo.Model.VO.GameIconVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class RedisGameIconService {
    private static final Logger logger = LoggerFactory.getLogger(RedisGameIconService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String ALL_GAME_ICON_KEY = "ALL_GAME_ICONS";
    public void SetAllGameIconsCache(List<GameIconVO> gameIconVOList) {
        logger.info("Setting up all game icons cache: {}");
        Jedis jedis = new Jedis("localhost");
        try {
            String gameIconVOList_json = objectMapper.writeValueAsString(gameIconVOList);
            jedis.set(ALL_GAME_ICON_KEY, gameIconVOList_json);
        } catch (Exception e) {
            logger.error("Failed to set all game icons cache: {}", e.getMessage(),e);
        } finally {
            logger.info("Closing the jedis connection:::");
            jedis.close();
        }
    }

    public List<GameIconVO> GetAllGameIconsCache() {
        logger.info("Getting all game icons cache: {}");
        Jedis jedis = new Jedis("localhost");
        try {
            String json = jedis.get(ALL_GAME_ICON_KEY);
            List<GameIconVO> res = objectMapper.readValue(json, List.class);
            return res;
        } catch (Exception e) {
            logger.error("Failed to get all game icons cache: {}", e.getMessage(),e);
        } finally {
            logger.info("Closing the jedis connection:::");
            jedis.close();
        }
        return null;
    }
}
