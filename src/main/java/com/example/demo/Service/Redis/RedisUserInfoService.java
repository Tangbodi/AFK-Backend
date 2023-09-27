package com.example.demo.Service.Redis;

import com.example.demo.Constant.Enum.ObjectNameEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class RedisUserInfoService {
    private static final Logger logger = LoggerFactory.getLogger(RedisUserInfoService.class);
    private static final String USER_SETTING = "USER_SETTING";
    private static final String SAVED_GAME = ObjectNameEnum.SAVED_GAME_SET.getTypeName();

    @Autowired
    private RedisMessageService redisMessageService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisUserFavoriteGameService redisUserFavoriteGameService;
    @Autowired
    private RedisUserSettingService redisUserSettingService;

    @Transactional
    public void DeleteUserInfoFromRedis(Long userId){
        logger.info("Deleting user info from Redis");
        try {
            redisMessageService.DeleteUnreadMessage(userId);
            redisUserFavoriteGameService.UpdateUserFavoriteGameFromCacheToDB(userId);
            redisUserSettingService.DeleteUserSettingCache(userId);
            logger.info("Deleted member: {}", userId.toString());
        } catch (Exception e) {
            logger.error("Failed to delete user info from Redis: {}", e.getMessage(), e);
        }
    }
}
