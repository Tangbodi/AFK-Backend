package com.example.demo.Service.Redis;

import com.example.demo.Model.DTO.UserSettingDTO;
import com.example.demo.Model.VO.UserSettingVO;
import com.example.demo.Service.UsersInfo.UserSettingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;

@Service
public class RedisUserSettingService {
    private static final Logger logger = LoggerFactory.getLogger(RedisUserSettingService.class);
    private static final String USER_SETTING = "USER_SETTING";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private RedisService redisService;
    @Lazy
    @Autowired
    private UserSettingService userSettingService;

    public void HandleUserSettingStrategy(UserSettingDTO userSettingDTO) throws IOException {
        logger.info("Handling user setting strategy");
        Long userId = userSettingDTO.getUserId();
        logger.info("User id: {}", userId);
        String key = USER_SETTING + ":::" + userId;
        //Get user setting list from Redis to update
        UserSettingVO userSettingVO = GetUserSettingCache(key, userId);
        //if user setting list is null, create new user setting list
        if (userSettingVO == null) {
            logger.info("No user setting found");
        } else {
            //if user setting is not null, update user setting list
            logger.info("Updating user setting");
            UpdateUserSetting(userSettingVO, userSettingDTO);
            //Set user setting list to Redis
            redisService.AddHashSet(key, userId.toString(), userSettingVO);
            UpdateUserSettingFromCacheToDB(userId);
        }
    }

    private void UpdateUserSetting(UserSettingVO userSettingVO, UserSettingDTO userSettingDTO) {
        logger.info("Updating user setting");
        String key = USER_SETTING + ":::" + userSettingDTO.getUserId();
        switch (userSettingDTO.getType()) {
            case "commentOnPost":
                userSettingVO.setCommentOnPost(userSettingDTO.getStatus());
                break;
            case "likeOnComment":
                userSettingVO.setLikeOnComment(userSettingDTO.getStatus());
                break;
            case "likeOnPost":
                userSettingVO.setLikeOnPost(userSettingDTO.getStatus());
                break;
            case "postOnSavedGame":
                userSettingVO.setPostOnSavedGame(userSettingDTO.getStatus());
                break;
            case "replyOnComment":
                userSettingVO.setReplyOnComment(userSettingDTO.getStatus());
                break;
            case "saveOnPost":
                userSettingVO.setSaveOnPost(userSettingDTO.getStatus());
                break;
            case "mentionOfUsername":
                userSettingVO.setMentionOfUsername(userSettingDTO.getStatus());
                break;
            default:
                break;
        }
    }

    public UserSettingVO GetUserSettingCache(String key, Long userId) throws IOException {
        logger.info("Getting user setting cache");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String userSettingVO_json = jedis.hget(key, userId.toString());
            if (userSettingVO_json != null) {
                UserSettingVO userSettingVO = objectMapper.readValue(userSettingVO_json, new TypeReference<UserSettingVO>() {
                });
                return userSettingVO;
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Failed to get user setting cache: {}", e.getMessage(), e);
            return null;
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }
    @Async("MultiExecutor")
    public void UpdateUserSettingFromCacheToDB(Long userId) throws IOException {
        logger.info("Updating user setting from cache to DB");
        String key = USER_SETTING + ":::" + userId;
        UserSettingVO userSettingVO = GetUserSettingCache(key, userId);
        userSettingService.SaveUserSetting(userSettingVO, userId);
    }
    @Async("MultiExecutor")
    public void DeleteUserSettingCache(Long userId) throws IOException {
        logger.info("Deleting user setting cache");
        String key = USER_SETTING + ":::" + userId;
        UserSettingVO userSettingVO = GetUserSettingCache(key, userId);
        userSettingService.SaveUserSetting(userSettingVO, userId);
        redisService.DeleteMember(USER_SETTING + ":::" + userId.toString(), userId.toString());
    }
}
