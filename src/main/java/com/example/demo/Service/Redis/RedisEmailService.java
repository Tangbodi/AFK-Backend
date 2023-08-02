package com.example.demo.Service.Redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.UUID;

@Service
public class RedisEmailService {
    private static final Logger logger = LoggerFactory.getLogger(RedisUsernameService.class);

    private String GenerateEmailToken() {
        logger.info("Generating email token");
        String token = UUID.randomUUID().toString();
        logger.info("Email token generated: {}" + token);
        return UUID.randomUUID().toString();
    }

    public String SetUpdateEmailCache(String newEmail) {
        logger.info("Setting up redis cache for email update: {}" + newEmail);
        Jedis jedis = new Jedis("localhost");
        try {
            String token = GenerateEmailToken();
            jedis.mset(token, newEmail);
            jedis.expire(token, 600);
            logger.info("Redis cache for email update set up successfully: {}" + newEmail);
            return token;
        } catch (Exception e) {
            logger.error("Failed to set up redis cache for email update", e);
        } finally {
            logger.info("Closing the jedis connection:::");
            jedis.close();
        }
        return null;
    }

    public boolean CheckUpdateEmailCache(String token) {
        logger.info("Checking redis cache for email update: {}" + token);
        Jedis jedis = new Jedis("localhost");
        try {
            if (jedis.exists(token)) {
                logger.info("Update email cache exists: {}" + token);
                return true;
            } else {
                logger.info("Update email cache doesn't exist: {}" + token);
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to check update email cache", e);
        } finally {
            logger.info("Closing the jedis connection:::");
            jedis.close();
        }
        return false;
    }

    public String GetEmailByToken(String token) {
        logger.info("Getting email by token: {}" + token);
        Jedis jedis = new Jedis("localhost");
        try {
            return jedis.get(token);
        } catch (Exception e) {
            // Handle exceptions
        } finally {
            logger.info("Closing the jedis connection:::");
            jedis.close(); // Close the Jedis connection
        }
        return null;
    }

    public void DeleteEmailByToken(String token) {
        logger.info("Deleting email by token: {}" + token);
        Jedis jedis = new Jedis("localhost");
        try {
            jedis.del(token); // Delete the token-email pair from Redis
            logger.info("Email deleted by token: {}" + token);
        } catch (Exception e) {
            // Handle exceptions
        } finally {
            logger.info("Closing the jedis connection:::");
            jedis.close(); // Close the Jedis connection
        }
    }
}
