package com.example.demo.Service.Redis;

import com.example.demo.Mapper.Repository.MessageRepository;
import com.example.demo.Model.VO.MessageVO;
import com.example.demo.Model.VO.NewsVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class RedisMessageService {
    private static final Logger logger = LoggerFactory.getLogger(RedisMessageService.class);
    private static final String MESSAGE_MENTION_KEY = "UNREAD:";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private RedisService redisService;

    public void GetUnreadMessageByUserId(Long userId) {
        logger.info("Setting unread messages by user id");
        try {
            List<Map<Short, Object>> messagesList = messageRepository.getUnreadMessagesByUserId(userId);
            SetUnreadMessageToRedis(messagesList, userId);
        } catch (Exception e) {
            logger.error("Failed to get unread messages by user id", e.getMessage(), e);
        }
    }

    public void SetUnreadMessageToRedis(List<Map<Short, Object>> messagesList, Long userId) {
        logger.info("Setting unread message cache: userId = {}", userId);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            List<MessageVO> unreadMessageVOList = new ArrayList<>();
            if(!messagesList.isEmpty()){
                for (Map<Short, Object> map : messagesList) {
                    MessageVO messageVO = new MessageVO();
                    messageVO.setMessageId(map.get("message_id").toString());
                    messageVO.setObjectId(map.get("object_id").toString());
                    messageVO.setFromUid(map.get("from_uid").toString());
                    messageVO.setToUid(map.get("to_uid").toString());
                    messageVO.setFromUsername(map.get("from_username").toString());
                    messageVO.setFromAvatarUrl(map.get("from_avatar_url").toString());
                    messageVO.setContent(map.get("content").toString());
                    messageVO.setTypeId(map.get("type_id").toString());
                    messageVO.setCreatedAt(map.get("created_at").toString());
                    unreadMessageVOList.add(messageVO);
                }
            } else {
                //
            }
            String new_unread_json = objectMapper.writeValueAsString(unreadMessageVOList);
            jedis.set(MESSAGE_MENTION_KEY + userId, new_unread_json);

        } catch (Exception e) {
            logger.error("Failed to set unread message cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis)
                logger.info("Closing the jedis connection:::");
            jedis.close();
        }
    }

    public List<MessageVO> GetUnreadMessageFromRedis(Long userId) {
        logger.info("Getting unread message cache");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String unread_json = jedis.get(MESSAGE_MENTION_KEY + userId);
            List<MessageVO> unreadMessageVOList = objectMapper.readValue(unread_json,new TypeReference<List<MessageVO>>() {
            });
            return unreadMessageVOList;
        } catch (Exception e) {
            logger.error("Failed to get unread message cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis)
                logger.info("Closing the jedis connection:::");
            jedis.close();
        }
        return Collections.emptyList();
    }

    public void DeleteUnreadMessage(Long userId) {
        logger.info("Deleting user read status cache: userId = {}", userId);
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
