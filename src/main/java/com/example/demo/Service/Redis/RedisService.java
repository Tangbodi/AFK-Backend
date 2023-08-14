package com.example.demo.Service.Redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {
    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);
    @Autowired
    private JedisPool jedisPool;
    public boolean CacheExists(String cacheKey) {
        logger.info("Checking if cache exists: cacheKey = {}", cacheKey);
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            if (jedis.exists(cacheKey)) {
                logger.info("Cache exists: cacheKey = {}", cacheKey);
                return true;
            } else {
                logger.info("Cache doesn't exist: cacheKey = {}", cacheKey);
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to check cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis)
                logger.info("Closing the jedis connection:::");
            jedis.close();
        }
        return false;
    }
}
