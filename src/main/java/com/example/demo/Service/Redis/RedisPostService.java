package com.example.demo.Service.Redis;

import com.example.demo.Model.DTO.PostDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisPostService {
    private static final Logger logger = LoggerFactory.getLogger(RedisPostService.class);
    private static final String POST_USER_KEY = "POST:";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JedisPool jedisPool;

    public void SetPostCache(PostDTO postDTO) {
        logger.info("Setting up post cache: {}");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String post_json = objectMapper.writeValueAsString(postDTO);
            jedis.set(POST_USER_KEY + postDTO.getUserId(), post_json);
            jedis.expire(POST_USER_KEY + postDTO.getUserId(), 600);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }

//    public String GetPostIdViaCache(String userId) {
//        logger.info("Getting the post id via cache");
//        Jedis jedis = null;
//        try {
//            jedis = jedisPool.getResource();
//            String post_json = jedis.get(POST_USER_KEY + userId);
//            Post post = objectMapper.readValue(post_json, Post.class);
//            return post.getId();
//        } catch (JsonMappingException e) {
//            throw new RuntimeException(e);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        } finally {
//            if (null != jedis) {
//                logger.info("Closing the jedis connection:::");
//                jedis.close();
//            }
//        }
//    }

    public PostDTO GetPostDTOViaCache(Long userId) {
        logger.info("Getting the post via cache");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String post_json = jedis.get(POST_USER_KEY + userId);
            PostDTO postDTO = objectMapper.readValue(post_json, PostDTO.class);
            if(null != postDTO){
                logger.info("PostDTO retrieved from cache");
            } else {
                logger.info("PostDTO not found in cache");
            }
            return postDTO;
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }

    public boolean CheckPostCache(Long userId) {
        logger.info("Checking the post cache");
        Jedis jedis = null;
        boolean cacheExists = false;
        try {
            jedis = jedisPool.getResource();
            String cacheKey = POST_USER_KEY + userId;
            cacheExists = jedis.exists(cacheKey);
            if (cacheExists) {
                logger.debug("Post cache for userId {} exists", userId);
            } else {
                logger.debug("Post cache for userId {} doesn't exist", userId);
            }
        } catch (Exception e) {
            logger.error("Failed to check post cache", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
        return cacheExists;
    }

    public void DeletePostCache(Long userId) {
        logger.info("Deleting the post cache");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(POST_USER_KEY + userId);
            logger.info("Post cache deleted");
        } catch (Exception e) {
            logger.error("Failed to delete post cache", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }
}
