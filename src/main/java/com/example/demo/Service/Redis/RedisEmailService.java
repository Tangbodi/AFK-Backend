package com.example.demo.Service.Redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.UUID;

@Service
public class RedisEmailService {
    private static final Logger logger = LoggerFactory.getLogger(RedisUsernameService.class);
    @Autowired
    private JedisPool jedisPool;

    private static String GenerateEmailToken() {
        logger.info("Generating email token: {}");
        String token = UUID.randomUUID().toString();
        logger.info("Email token generated: {}" + token);
        return token;
    }

    public String SetUpdateEmailCache(String newEmail) {
        logger.info("Setting up redis cache for email update: {}" + newEmail);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String token = GenerateEmailToken();
            jedis.mset(token, newEmail);
            jedis.expire(token, 600);
            logger.info("Redis cache for email update set up successfully: {}" + newEmail);
            return token;
        } catch (Exception e) {
            logger.error("Failed to set up redis cache for email update: {}", e.getMessage(), e);
            return null;
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }

    public boolean CheckUpdateEmailCache(String token) {
        logger.info("Checking redis cache for email update: {}" + token);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(token)) {
                logger.info("Update email cache exists: {}" + token);
                return true;
            } else {
                logger.info("Update email cache doesn't exist: {}" + token);
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to check update email cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
        return false;
    }

    public String GetEmailByToken(String token) {
        logger.info("Getting email by token: {}" + token);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(token);
        } catch (Exception e) {
            logger.error("Failed to get email by token", e.getMessage(), e);
            // Handle exceptions
        } finally {
            if (null != jedis){
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
        return null;
    }

    public void DeleteEmailByToken(String token) {
        logger.info("Deleting email by token: {}" + token);
        Jedis jedis = jedisPool.getResource();
        try {
            jedis = jedisPool.getResource();
            jedis.del(token); // Delete the token-email pair from Redis
            logger.info("Email deleted by token: {}" + token);
        } catch (Exception e) {
            logger.error("Failed to delete email by token: {}", e.getMessage(), e);
            // Handle exceptions
        } finally {
            if (null != jedis){
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }
}
