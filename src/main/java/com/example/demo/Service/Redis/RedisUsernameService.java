package com.example.demo.Service.Redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Service
public class RedisUsernameService {
    private static final Logger logger = LoggerFactory.getLogger(RedisUsernameService.class);
    private static final String USER_EXISTS_KEY = "EXISTS:";
    private static final String EMAIL_VALIDATION = "EMAIL_VALIDATION:";

    @Autowired
    private JedisPool jedisPool;

    public void SetUsernameExistsCache(String username) {
        logger.info("Setting up username exists cache: {}");
        Jedis jedis = new Jedis("localhost", 6379);
        jedis.auth("960c3dac4fa81b4204779fd16ad7c954f95942876b9c4fb1a255667a9dbe389d");
        try {
            jedis = jedisPool.getResource();
            jedis.set(USER_EXISTS_KEY + username, "true");
            jedis.expire(USER_EXISTS_KEY + username, 30);
        } catch (Exception e) {
            logger.error("Failed to set username exists cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
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
            if (jedis.exists(USER_EXISTS_KEY + username)) {
                logger.info("Username cache exists: {}");
                return true;
            } else {
                logger.info("Username cache doesn't exist: {}");
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to check username exists cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
        return false;
    }
    public boolean CheckEmailValidationCacheByUsername(String username) {
        logger.info("Checking user email validation cache: {}");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(EMAIL_VALIDATION + username)) {
                logger.info("User email validation cache exists: {}");
                return true;
            } else {
                logger.info("User email validation cache doesn't exist: {}");
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to check user email validation cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
        return false;
    }
    public void DeleteEmailValidationCacheByUsername(String username) {
        logger.info("Deleting user email validation cache: {}" + username);
        Jedis jedis = jedisPool.getResource();
        try {
            jedis = jedisPool.getResource();
            jedis.del(EMAIL_VALIDATION + username); // Delete the token-email pair from Redis
            logger.info("Email deleted by token: {}" + username);
        } catch (Exception e) {
            logger.error("Failed to delete email by token: {}", e.getMessage(), e);
            // Handle exceptions
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }
}
