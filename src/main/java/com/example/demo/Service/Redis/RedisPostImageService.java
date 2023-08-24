package com.example.demo.Service.Redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

//public class RedisPostImageService {
//    private static final Logger logger = LoggerFactory.getLogger(RedisPostImageService.class);
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//
//    private static final String POST_IMAGE_KEY = "POST_IMAGE:";
//    @Autowired
//    private JedisPool jedisPool;
//
//    public void SetPostImageCache(Long userId, List<MultipartFile> images) {
//        logger.info("Setting up post image cache: {}");
//        Jedis jedis = null;
//        try {
//            jedis = jedisPool.getResource();
//            String post_image_json = objectMapper.writeValueAsString(postDTO);
//            jedis.set(POST_USER_KEY + postDTO.getUserId(), post_json);
//            jedis.expire(POST_USER_KEY + postDTO.getUserId(), 600);
//        }
//    }
//}
