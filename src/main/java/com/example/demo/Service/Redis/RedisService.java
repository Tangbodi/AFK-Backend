package com.example.demo.Service.Redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service

public class RedisService {
    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String GenerateEmailToken() {

        return UUID.randomUUID().toString();
    }

    public String SetUpdateEmailCache(String newEmail) {
        logger.info("Setting up redis cache for email update: {}" + newEmail);
        Jedis jedis = new Jedis("localhost");
        try {
            Map<String, String> tokenEmail = new HashMap<String, String>();
            String token = GenerateEmailToken();
            tokenEmail.put(token, newEmail);
            String tokenEmail_json = objectMapper.writeValueAsString(tokenEmail);
            jedis.mset(token, newEmail);
            logger.info("Redis cache for email update set up successfully: {}" + newEmail);
            return token;
        } catch (Exception e) {
            logger.error("Failed to set up redis cache for email update", e);
        }
        return null;
    }
}
