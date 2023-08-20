package com.example.demo.Service.Redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisEmailService {
    private static final Logger logger = LoggerFactory.getLogger(RedisUsernameService.class);
    private static final String EMAIL_VALIDATION = "EMAIL_VALIDATION:";
    private static final String EMAIL_UPDATE = "EMAIL_UPDATE:";
    @Autowired
    private JedisPool jedisPool;

    public void SetEmailValidationCacheByToken(String token, String email) {
        logger.info("Setting email by token: {}" + token);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(EMAIL_VALIDATION + token, email);
            jedis.expire(EMAIL_VALIDATION + token, 180);
            logger.info("Email set by token successfully: {}" + token);
        } catch (Exception e) {
            logger.error("Failed to set email by token: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }

//    public void SetUpdateEmailCache(String userId, String newEmail) {
//        logger.info("Setting update email cache: {}" + newEmail);
//        Jedis jedis = null;
//        try {
//            jedis = jedisPool.getResource();
//            jedis.set(EMAIL_UPDATE + userId, newEmail);
//            jedis.expire(EMAIL_UPDATE + userId, 180);
//            logger.info("Redis cache for email update set up successfully: {}" + newEmail);
//
//        } catch (Exception e) {
//            logger.error("Failed to set up redis cache for email update: {}", e.getMessage(), e);
//        } finally {
//            if (null != jedis) {
//                logger.info("Closing the jedis connection:::");
//                jedis.close();
//            }
//        }
//    }

    public boolean CheckEmailValidationCacheByToken(String token) {
        logger.info("Checking email validation cache by token: {}" + token);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(EMAIL_VALIDATION + token)) {
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
            return jedis.get(EMAIL_VALIDATION + token);
        } catch (Exception e) {
            logger.error("Failed to get email by token", e.getMessage(), e);
            // Handle exceptions
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
        return null;
    }

    public void DeleteEmailValidationCacheByToken(String token) {
        logger.info("Deleting email validation cache by token: {}" + token);
        Jedis jedis = jedisPool.getResource();
        try {
            jedis = jedisPool.getResource();
            jedis.del(EMAIL_VALIDATION + token); // Delete the token-email pair from Redis
            logger.info("Email validation cache deleted by token: {}" + token);
        } catch (Exception e) {
            logger.error("Failed to delete email validation cache by token: {}", e.getMessage(), e);
            // Handle exceptions
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }

}
