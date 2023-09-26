package com.example.demo.Service.Redis;

import com.example.demo.Model.VO.UserSettingVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.List;

@Service
public class RedisUserSettingService {
    private static final Logger logger = LoggerFactory.getLogger(RedisUserSettingService.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private JedisPool jedisPool;

    public void SetUserSettingCache(String key, Long userId, List<UserSettingVO> userSettingVOList) throws JsonProcessingException {
        logger.info("Setting up user setting cache: {}", userId);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String userSettingVOList_json = objectMapper.writeValueAsString(userSettingVOList);
            jedis.hset(key, String.valueOf(userId), userSettingVOList_json);
        } catch (Exception e) {
            logger.error("Failed to set user setting cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
    }

    //    public void HandleUserSettingStrategy(UserSettingDTO userSettingDTO) throws IOException {
//        logger.info("Handling user setting strategy");
//        Long userId = userSettingVO.getUserId();
//        String typeName = userSettingVO.getTypeName();
//        String key = typeName + ":::" + userId;
//        //Get user setting list from Redis to update
//        List<UserSettingVO> userSettingVOList = GetUserSettingCache(key, userId);
//        //if user setting list is null, create new user setting list
//        if (userSettingVOList == null) {
//            logger.info("Creating new user setting list");
//            userSettingVOList = CreateUserSettingList(userSettingVO);
//        } else {
//            //if user setting list is not null, update user setting list
//            logger.info("Updating user setting list");
//            UpdateUserSettingList(userSettingVOList, userSettingVO);
//        }
//        //Set user setting list to Redis
//        SetUserSettingCache(key, userId, userSettingVOList);
//    }
//    @Async("MultiExecutor")
//    public void UpdateUserSettingFromCacheToDB(Long userId) throws IOException {
//        logger.info("Updating user favorite game from cache to DB");
//        //HashSet key is SAVED_GAME:::userId in Redis
//        List<UserSettingVO> userSettingVOList = redis.GetUserFavoriteGameCache(SAVED_GAME + ":::" + userId.toString(), userId);
//        userFavoriteGameService.SetUserFavoriteGame(userFavoriteGameVOList, userId);
//        redisService.DeleteMember(SAVED_GAME + ":::" + userId.toString(), userId.toString());
//        logger.info("Deleted member: {}", userId.toString());
//        if (redisService.NumOfMembers(userId.toString()) == 0) {
//            redisService.RemoveHashSet(SAVED_GAME, userId.toString());
//            logger.info("Removed hash set: {}", SAVED_GAME);
//        } else {
//            //
//        }
//    }

    public List<UserSettingVO> GetUserSettingCache(String key, Long userId) throws IOException {
        logger.info("Getting user setting cache");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String userSettingVOList_json = jedis.hget(key, String.valueOf(userId));
            if (userSettingVOList_json != null) {
                List<UserSettingVO> userSettingVOList = objectMapper.readValue(userSettingVOList_json, new TypeReference<List<UserSettingVO>>() {
                });
                return userSettingVOList;
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
}
