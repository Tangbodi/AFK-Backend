package com.example.demo.Service.Redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.Set;


@Service
public class RedisLikeSaveService {
    private static final Logger logger = LoggerFactory.getLogger(RedisLikeSaveService.class);

    @Autowired
    private JedisPool jedisPool;


    public Boolean MemberExists(String key, Object value) {
        logger.info("Checking exists in Redis: {}", key, ":::", value);
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

    public void AddSet(String typeNameInSet, Object objectId) {
        logger.info("Adding set to Redis: {}", typeNameInSet, ":::", objectId);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.sadd(typeNameInSet, String.valueOf(objectId));
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
        logger.info("Adding hash set to Redis: {}", key, ":::", hashKey, ":::", value);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hset(key, hashKey, String.valueOf(value));
        } catch (Exception e) {
            logger.error("Failed to set username exists cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }

    public void DeleteMember(String key, String hashKey) {
        logger.info("Deleting member from Redis: {}", key, ":::", hashKey);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hdel(key, hashKey);
            logger.info("Deleted member from Redis: {}", key, ":::", hashKey);
        } catch (Exception e) {
            logger.error("Failed to set username exists cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }

    public Long NumOfMembers(String key) {
        logger.info("Getting number of members from Redis");
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
        return 0L;
    }

    public void RemoveHashSet(String key, Object value) {
        logger.info("Removing hash set from Redis: {}", key, ":::", value);
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

    public long GetHashSetSize(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hlen(key);
        }
    }

    public Map<String, String> GetHashValue(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hgetAll(key);
        }
    }
}
