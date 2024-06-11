package com.example.demo.Service.Redis;

import com.example.demo.Mapper.Repository.MessageRepository;
import com.example.demo.Model.VO.MessageVO;
import com.example.demo.Model.VO.NewsVO;
import com.example.demo.Util.DateTimeConverter;
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
    private JedisPool jedisPool;
    @Autowired
    private RedisService redisService;

    public void SetUnreadMessageToRedis(List<Map<Short, Object>> messagesList, Long userId) {
        logger.info("Setting unread message cache: userId = {}", userId);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            List<MessageVO> unreadMessageVOList = new ArrayList<>();
            if(!messagesList.isEmpty()){
                for (Map<Short, Object> map : messagesList) {
                    MessageVO messageVO = new MessageVO();
                    messageVO.setMessageId(String.valueOf(map.get("message_id")));
                    messageVO.setGenreId(String.valueOf(map.get("genre_id")));
                    messageVO.setGameId(String.valueOf(map.get("game_id")));
                    messageVO.setPostId(String.valueOf(map.get("post_id")));
                    messageVO.setCommentReplyId(String.valueOf(map.get("comment_reply_id")));
                    messageVO.setFromUid(String.valueOf(map.get("from_uid")));
                    messageVO.setToUid(String.valueOf(map.get("to_uid")));
                    messageVO.setFromUsername(String.valueOf(map.get("from_username")));
                    messageVO.setFromAvatarUrl(String.valueOf(map.get("from_avatar_url")));
                    messageVO.setContent(String.valueOf(map.get("content")));
                    messageVO.setTypeId(String.valueOf(map.get("type_id")));
                    String formattedDateTime = DateTimeConverter.DateTimeConvertFromString(String.valueOf(map.get("created_at")));
                    messageVO.setCreatedAt(formattedDateTime);
                    unreadMessageVOList.add(messageVO);
                }
            } else {
                //
            }
            String new_unread_json = objectMapper.writeValueAsString(unreadMessageVOList);
            jedis.set(MESSAGE_MENTION_KEY + userId, new_unread_json);
            jedis.expire(MESSAGE_MENTION_KEY + userId, 1800);

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
    public void UpdateUnreadMessageFromRedis(List<MessageVO> updatedMessage){
        logger.info("Updating unread message cache");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String new_unread_json = objectMapper.writeValueAsString(updatedMessage);
            Long userId = Long.valueOf(updatedMessage.get(0).getToUid());
            jedis.set(MESSAGE_MENTION_KEY + userId, new_unread_json);
            jedis.expire(MESSAGE_MENTION_KEY + userId, 1800);
        } catch (Exception e) {
            logger.error("Failed to update unread message cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis)
                logger.info("Closing the jedis connection:::");
            jedis.close();
        }
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
