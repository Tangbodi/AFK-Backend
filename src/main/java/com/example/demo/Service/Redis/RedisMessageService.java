package com.example.demo.Service.Redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisMessageService {
    private static final Logger logger = LoggerFactory.getLogger(RedisMessageService.class);
    private static final String MESSAGE_MENTION_KEY = "UNREAD:";

    @Autowired
    private JedisPool jedisPool;

    public void SetUserReadStatus(String userId) {
        logger.info("Setting user read status: userId = {}", userId);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(MESSAGE_MENTION_KEY+userId, "0");
        } catch (Exception e) {
            logger.error("Failed to set user read status: {}", e.getMessage(), e);
        } finally {
            if (null != jedis)
                logger.info("Closing the jedis connection:::");
            jedis.close();
        }
    }
    public void DeleteUserReadStatus(String userId) {
        logger.info("Deleting user read status: userId = {}", userId);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(MESSAGE_MENTION_KEY+userId);
        } catch (Exception e) {
            logger.error("Failed to delete user read status: {}", e.getMessage(), e);
        } finally {
            if (null != jedis)
                logger.info("Closing the jedis connection:::");
            jedis.close();
        }
    }
}
