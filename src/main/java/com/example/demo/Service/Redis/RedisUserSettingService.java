package com.example.demo.Service.Redis;

import com.example.demo.Model.DTO.UserSettingDTO;
import com.example.demo.Model.VO.ActivityVO;
import com.example.demo.Model.VO.RecommendationVO;
import com.example.demo.Service.UserSettings.UserSettingService;
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
import java.util.*;

@Service
public class RedisUserSettingService {
    private static final Logger logger = LoggerFactory.getLogger(RedisUserSettingService.class);
    private static final String USER_SETTING = "USER_SETTING";
    private static final String ACTIVITY = "activity";
    private static final String RECOMMENDATION = "recommendation";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Set<String> ActivitySet = new HashSet<>(Arrays.asList("commentOnPost", "likeOnComment", "likeOnPost", "postOnSavedGame", "replyOnComment", "saveOnPost", "mentionOfUsername"));
    private static final Set<String> RecommendationSet = new HashSet<>(Arrays.asList("afkAnnouncement", "featuredContent", "trendingPost", "communityRecommendation"));
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
        Map<String, Object> userSettingVOMap = GetUserSettingCache(key, userId);
        logger.info("User setting list: {}", userSettingVOMap);
        //if user setting list is null, create new user setting list
        if (userSettingVOMap == null) {
            logger.info("No user setting found");
        } else {
            //if user setting is not null, update user setting list
            UpdateUserSetting(userSettingVOMap, userSettingDTO);
            //Set user setting list to Redis
            redisService.AddHashSet(key, userId.toString(), userSettingVOMap);
            UpdateUserSettingFromCacheToDB(userId, userSettingDTO);
        }
    }

    @Async("MultiExecutor")
    private void UpdateUserSetting(Map<String, Object> userSettingVOMap, UserSettingDTO userSettingDTO) {
        logger.info("Updating user setting");
        String type = userSettingDTO.getType();
        Object VOMap;
        if (ActivitySet.contains(type)) {;
            VOMap = userSettingVOMap.get(ACTIVITY);
            if (VOMap instanceof LinkedHashMap) {
                LinkedHashMap<String,Integer> activityVOMap = (LinkedHashMap<String, Integer>) VOMap;
                UpdateActivitySetting(activityVOMap, userSettingDTO);
                userSettingVOMap.put(ACTIVITY, activityVOMap);
            } else {
                logger.info("ActivityVO is null");
            }
        } else if (RecommendationSet.contains(type)) {
            VOMap = userSettingVOMap.get(RECOMMENDATION);
            if ( VOMap instanceof LinkedHashMap) {
                LinkedHashMap<String,Integer> recommendationVOMap = (LinkedHashMap<String, Integer>) VOMap;
                UpdateRecommendationSetting(recommendationVOMap, userSettingDTO);
                userSettingVOMap.put(RECOMMENDATION, recommendationVOMap);
            } else {
                logger.info("RecommendationVO is null");
            }
        } else{
            //
        }
    }

    private void UpdateActivitySetting(LinkedHashMap<String,Integer> activeVOMap, UserSettingDTO userSettingDTO) {
        logger.info("Updating activity setting");
        switch (userSettingDTO.getType()) {
            case "commentOnPost":
                activeVOMap.put("commentOnPost", userSettingDTO.getStatus());
                break;
            case "likeOnComment":
                activeVOMap.put("likeOnComment", userSettingDTO.getStatus());
                break;
            case "likeOnPost":
                activeVOMap.put("likeOnPost", userSettingDTO.getStatus());
                break;
            case "postOnSavedGame":
                activeVOMap.put("postOnSavedGame", userSettingDTO.getStatus());
                break;
            case "replyOnComment":
                activeVOMap.put("replyOnComment", userSettingDTO.getStatus());
                break;
            case "saveOnPost":
                activeVOMap.put("saveOnPost", userSettingDTO.getStatus());
                break;
            case "mentionOfUsername":
                activeVOMap.put("mentionOfUsername", userSettingDTO.getStatus());
                break;
            default:
                break;
        }
    }

    private void UpdateRecommendationSetting( LinkedHashMap<String,Integer> recommendationVOMap, UserSettingDTO userSettingDTO) {
        logger.info("Updating recommendation setting");
        switch (userSettingDTO.getType()) {
            case "afkAnnouncement":
                recommendationVOMap.put("afkAnnouncement", userSettingDTO.getStatus());
            break;
            case "featuredContent":
                recommendationVOMap.put("featuredContent", userSettingDTO.getStatus());
                break;
            case "trendingPost":
                recommendationVOMap.put("trendingPost", userSettingDTO.getStatus());
                break;
            case "communityRecommendation":
                recommendationVOMap.put("communityRecommendation", userSettingDTO.getStatus());
                break;
            default:
                break;
        }
    }

    public Map<String, Object> GetUserSettingCache(String key, Long userId) throws IOException {
        logger.info("Getting user setting cache");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String userSettingVO_json = jedis.hget(key, userId.toString());
            if (userSettingVO_json != null) {
                Map<String, Object> userSettingVOMap = objectMapper.readValue(userSettingVO_json, new TypeReference<Map<String, Object>>() {
                });
                return userSettingVOMap;
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
    public void UpdateUserSettingFromCacheToDB(Long userId, UserSettingDTO userSettingDTO) throws IOException {
        logger.info("Updating user setting from cache to DB");
        userSettingService.UpdateUserSettingFromCacheToDB(userId, userSettingDTO);
    }

    @Async("MultiExecutor")
    public void DeleteUserSettingCache(Long userId) throws IOException {
        logger.info("Deleting user setting cache");
        String key = USER_SETTING + ":::" + userId;
        Map<String, Object> userSettingVOMap = GetUserSettingCache(key, userId);
        userSettingService.SaveUserSetting(userId, userSettingVOMap);
        redisService.DeleteMember(USER_SETTING + ":::" + userId.toString(), userId.toString());
    }
}
