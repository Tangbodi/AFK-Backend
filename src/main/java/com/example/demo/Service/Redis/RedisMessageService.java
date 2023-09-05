package com.example.demo.Service.Redis;

import com.example.demo.Model.VO.MessageVO;
import com.example.demo.Model.VO.NewsVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;
import java.util.List;

@Service
public class RedisMessageService {
    private static final Logger logger = LoggerFactory.getLogger(RedisMessageService.class);
    private static final String MESSAGE_MENTION_KEY = "UNREAD:";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private RedisService redisService;

    public void SetUnreadMessage(MessageVO messageVO) {
        logger.info("Setting user read status: userId = {}", messageVO.getToUid());
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (redisService.CacheExists(MESSAGE_MENTION_KEY + messageVO.getToUid())) {
                logger.info("User unread cache exists: userId = {}", messageVO.getToUid());
                String unread_json = jedis.get(MESSAGE_MENTION_KEY + messageVO.getToUid());
                List<MessageVO> unreadMessageVOList = objectMapper.readValue(unread_json, List.class);
                unreadMessageVOList.add(messageVO);
                String new_unread_json = objectMapper.writeValueAsString(unreadMessageVOList);
                jedis.set(MESSAGE_MENTION_KEY + messageVO.getToUid(), new_unread_json);
            } else {
                logger.info("User unread cache doesn't exist: userId = {}", messageVO.getToUid());
                logger.info("Creating new user unread cache: userId = {}", messageVO.getToUid());
                List<MessageVO> messageVOList = List.of(messageVO);
                String unread_json = objectMapper.writeValueAsString(messageVOList);
                jedis.set(MESSAGE_MENTION_KEY + messageVO.getToUid(), unread_json);
            }
        } catch (Exception e) {
            logger.error("Failed to set user read status: {}", e.getMessage(), e);
        } finally {
            if (null != jedis)
                logger.info("Closing the jedis connection:::");
            jedis.close();
        }
    }
    public List<MessageVO> GetUnreadMessage(Long userId){
        logger.info("Getting unread message");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String json = jedis.get(MESSAGE_MENTION_KEY+userId);
            List<MessageVO> messageVOList = objectMapper.readValue(json, List.class);
            return messageVOList;
        } catch (Exception e) {
            logger.error("Failed to set user read status: {}", e.getMessage(), e);
        } finally {
            if (null != jedis)
                logger.info("Closing the jedis connection:::");
            jedis.close();
        }
        return Collections.emptyList();
    }

    public void DeleteUnreadMessage(Long userId) {
        logger.info("Deleting user read status: userId = {}", userId);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(MESSAGE_MENTION_KEY + userId);
        } catch (Exception e) {
            logger.error("Failed to delete user read status: {}", e.getMessage(), e);
        } finally {
            if (null != jedis)
                logger.info("Closing the jedis connection:::");
            jedis.close();
        }
    }
}
