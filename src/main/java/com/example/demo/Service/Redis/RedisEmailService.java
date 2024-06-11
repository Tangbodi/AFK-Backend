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

    @Autowired
    private JedisPool jedisPool;

    public void SetEmailValidationCacheByToken(String token, String email) {
        logger.info("Setting email by token: {}" + token);
        Jedis jedis = new Jedis("localhost", 6379);
        jedis.auth("960c3dac4fa81b4204779fd16ad7c954f95942876b9c4fb1a255667a9dbe389d");
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
    public void SetEmailValidationCache(String email) {
        logger.info("Setting up email validation cache: {}");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(EMAIL_VALIDATION + email, "true");
            jedis.expire(EMAIL_VALIDATION + email, 180);
        } catch (Exception e) {
            logger.error("Failed to set email validation cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
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
        return "";
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
    public void DeleteEmailValidationCacheByEmail(String email) {
        logger.info("Deleting email validation cache by email: {}" + email);
        Jedis jedis = jedisPool.getResource();
        try {
            jedis = jedisPool.getResource();
            jedis.del(EMAIL_VALIDATION + email); // Delete the token-email pair from Redis
            logger.info("Email validation cache deleted by email: {}" + email);
        } catch (Exception e) {
            logger.error("Failed to delete email validation cache by email: {}", e.getMessage(), e);
            // Handle exceptions
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }
}
