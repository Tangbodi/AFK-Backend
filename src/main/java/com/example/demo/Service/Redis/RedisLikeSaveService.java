package com.example.demo.Service.Redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
public class RedisLikeSaveService {
    private static final Logger logger = LoggerFactory.getLogger(RedisLikeSaveService.class);

    @Autowired
    private JedisPool jedisPool;


    public Boolean MemberExists(String key, Object value) {
        logger.info("Checking exists");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sismember(key, String.valueOf(value));
        } catch (Exception e) {
            logger.error("Failed to set username exists cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
        return false;
    }
    public void AddSet(String key, Object value) {
        logger.info("Adding set");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.sadd(key, String.valueOf(value));
        } catch (Exception e) {
            logger.error("Failed to set username exists cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }

    public void AddHashSet(String key, String hashKey, Object value) {
        logger.info("Adding hash set");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hset(key, hashKey, (String) value);
        } catch (Exception e) {
            logger.error("Failed to set username exists cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }
    public void DeleteMember(String key, String hashKey){
        logger.info("Deleting member");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hdel(key, hashKey);
        } catch (Exception e) {
            logger.error("Failed to set username exists cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }
    public Long NumOfMembers(String key){
        logger.info("Getting number of members");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long num = jedis.hlen(key);
            return num;
        } catch (Exception e) {
            logger.error("Failed to set username exists cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
        return null;
    }
    public void RemoveHashSet(String key, Object value){
        logger.info("Removing hash set");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.srem(key, String.valueOf(value));
        } catch (Exception e) {
            logger.error("Failed to set username exists cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }
    public Set<String> GetAllSetMembers(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smembers(key);
        }
    }
    public Map<String, String> GetHashValue(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hgetAll(key);
        }
    }
}
