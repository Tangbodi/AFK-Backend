package com.example.demo.Service.Redis;

import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Constant.Enum.StatusEnum;
import com.example.demo.Model.DTO.UserLikeSaveDTO;
import com.example.demo.Model.VO.GameIconVO;
import com.example.demo.Model.VO.UserFavoriteGameVO;
import com.example.demo.Service.UserFavoriteGame.UserFavoriteGameService;
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
import java.util.List;

@Service
public class RedisUserFavoriteGameService {
    private static final Logger logger = LoggerFactory.getLogger(RedisUserFavoriteGameService.class);
    private static final String SAVED_GAME = ObjectNameEnum.SAVED_GAME_SET.getTypeName();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private RedisGameIconService redisGameIconService;
    @Lazy
    @Autowired
    private UserFavoriteGameService userFavoriteGameService;
    @Autowired
    private RedisService redisService;

    public void HandleUserFavoriteGameStrategy(UserLikeSaveDTO userLikeSaveDTO) throws IOException {
        logger.info("Handling user favorite game strategy");
        Long userId = userLikeSaveDTO.getUserId();
        Short gameId = userLikeSaveDTO.getObjectId().shortValue();
        String typeName = ObjectNameEnum.GetTypeName(userLikeSaveDTO.getTypeId());
        String key = typeName + ":::" + userId;
        Integer status = userLikeSaveDTO.getStatus();
//        if (!redisService.MemberExists(key, userId)) {
//            logger.info("User favorite game list doesn't exist in Redis, getting from DB");
//            GetUserFavoriteGames(userId);
//        }
        //Get user favorite game list from Redis to update
        List<UserFavoriteGameVO> userFavoriteGameVOList = GetUserFavoriteGameCache(SAVED_GAME + ":::" + userId, userId);
        //Get all game icons list from Redis
        List<GameIconVO> gameIconVOList = redisGameIconService.GetAllGameIconsCache();
        //Find game in all game icons list
        GameIconVO gameIconVO = gameIconVOList.stream()
                .filter(gameIconVO1 -> gameIconVO1.getGameId().equals(gameId))
                .findFirst()
                .orElse(null);
        //if status is 1, add game to user favorite game list
        if (status == StatusEnum.TRUE.getCode()) {
            logger.info("Adding game to user favorite game list");
            //Add game to UserFavoriteGameVO
            UserFavoriteGameVO userFavoriteGameVO = new UserFavoriteGameVO();
            userFavoriteGameVO.setGenreId(gameIconVO.getGenreId());
            userFavoriteGameVO.setGameId(gameIconVO.getGameId());
            userFavoriteGameVO.setGameName(gameIconVO.getGameName());
            userFavoriteGameVO.setIconUrl(gameIconVO.getIconUrl());
            userFavoriteGameVOList.add(userFavoriteGameVO);
            redisService.AddHashSet(key, userId.toString(), userFavoriteGameVOList);
            logger.info("Added game to UserFavoriteGameVO");
        } else {
            //Remove game from UserFavoriteGameVO
            logger.info("Removing game from user favorite game list");
            userFavoriteGameVOList.removeIf(userFavoriteGameVO -> userFavoriteGameVO.getGameId().equals(gameId));
            redisService.AddHashSet(key, userId.toString(), userFavoriteGameVOList);
            logger.info("Removed game from user favorite game list");
        }
    }

    @Async("MultiExecutor")
    public void UpdateUserFavoriteGameFromCacheToDB(Long userId) throws IOException {
        logger.info("Updating user favorite game from cache to DB");
        List<UserFavoriteGameVO> userFavoriteGameVOList = GetUserFavoriteGameCache(SAVED_GAME + ":::" + userId, userId);
        //HashSet key is SAVED_GAME:::userId in Redis
        userFavoriteGameService.SetUserFavoriteGame(userFavoriteGameVOList, userId);
        redisService.DeleteMember(SAVED_GAME + ":::" + userId.toString(), userId.toString());
    }

    public List<UserFavoriteGameVO> GetUserFavoriteGameCache(String key, Long userId) throws IOException {
        logger.info("Getting user favorite game cache: {}", userId);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String gameIconVOListJson = jedis.hget(key, userId.toString());
            if (gameIconVOListJson != null) {
                return objectMapper.readValue(gameIconVOListJson, new TypeReference<List<UserFavoriteGameVO>>() {
                });
            }
        } catch (Exception e) {
            logger.error("Failed to get user favorite game cache: {}", e.getMessage(), e);
        } finally {
            if (null != jedis) {
                logger.info("Closing the jedis connection:::");
                jedis.close();
            }
        }
        return null; // Handle cache miss or any other errors
    }
}
