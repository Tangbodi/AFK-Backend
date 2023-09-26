package com.example.demo.Service.Redis;

import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Constant.Enum.StatusEnum;
import com.example.demo.Model.DTO.UserLikeSaveDTO;
import com.example.demo.Model.VO.GameIconVO;
import com.example.demo.Model.VO.UserFavoriteGameVO;
import com.example.demo.Service.UserFavoriteGame.UserFavoriteGameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class RedisUserFavoriteGameService {
    private static final Logger logger = LoggerFactory.getLogger(RedisUserFavoriteGameService.class);
    private static final String SAVED_GAME = ObjectNameEnum.SAVED_GAME_SET.getTypeName();

    @Autowired
    private RedisGameIconService redisGameIconService;
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
        List<UserFavoriteGameVO> userFavoriteGameVOList = redisGameIconService.GetUserFavoriteGameCache(SAVED_GAME + ":::" + userId, userId);
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
            redisGameIconService.SetUserFavoriteGameCache(key, userId, userFavoriteGameVOList);
            logger.info("Added game to UserFavoriteGameVO");
        } else {
            //Remove game from UserFavoriteGameVO
            logger.info("Removing game from user favorite game list");
            userFavoriteGameVOList.removeIf(userFavoriteGameVO -> userFavoriteGameVO.getGameId().equals(gameId));
            redisGameIconService.SetUserFavoriteGameCache(key, userId, userFavoriteGameVOList);
            logger.info("Removed game from user favorite game list");
        }
    }
    @Async("MultiExecutor")
    public void UpdateUserFavoriteGameFromCacheToDB(Long userId) throws IOException {
        logger.info("Updating user favorite game from cache to DB");
        //HashSet key is SAVED_GAME:::userId in Redis
        List<UserFavoriteGameVO> userFavoriteGameVOList = redisGameIconService.GetUserFavoriteGameCache(SAVED_GAME + ":::" + userId.toString(), userId);
        userFavoriteGameService.SetUserFavoriteGame(userFavoriteGameVOList, userId);
        redisService.DeleteMember(SAVED_GAME + ":::" + userId.toString(), userId.toString());
        logger.info("Deleted member: {}", userId.toString());
        if (redisService.NumOfMembers(userId.toString()) == 0) {
            redisService.RemoveHashSet(SAVED_GAME, userId.toString());
            logger.info("Removed hash set: {}", SAVED_GAME);
        } else {
            //
        }
    }
}
