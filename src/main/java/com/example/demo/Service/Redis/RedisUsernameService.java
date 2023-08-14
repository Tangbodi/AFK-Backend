package com.example.demo.Service.Redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisUsernameService {
    private static final Logger logger = LoggerFactory.getLogger(RedisUsernameService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String ALL_GAME_ICON_KEY = "ALL_GAME_ICON";
    @Autowired
    private JedisPool jedisPool;

    public void SetUsernameExistsCache(String username) {
        logger.info("Setting up username exists cache: {}");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(username, "true");
            jedis.expire(username, 30);
        } catch (Exception e) {
            logger.error("Failed to set username exists cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis){
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }

    public boolean CheckUsernameExistsCache(String username) {
        logger.info("Checking username exists cache: {}");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(username)) {
                logger.info("Username cache exists: {}");
                return true;
            } else {
                logger.info("Username cache doesn't exist: {}");
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to check username exists cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis){
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
        return false;
    }
}
