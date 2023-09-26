package com.example.demo.Service.UserFavoriteGame;

import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Mapper.Repository.UserFavoriteGameRepository;
import com.example.demo.Model.Entity.UsersFavoriteGame;
import com.example.demo.Model.Entity.UsersFavoriteGameId;
import com.example.demo.Model.VO.UserFavoriteGameVO;
import com.example.demo.Service.Redis.RedisGameIconService;
import com.example.demo.Service.Redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class UserFavoriteGameService {
    private static final Logger logger = LoggerFactory.getLogger(UserFavoriteGameService.class);
    private static final String SAVED_GAME = ObjectNameEnum.SAVED_GAME_SET.getTypeName();
    @Autowired
    private UserFavoriteGameRepository userFavoriteGameRepository;
    @Autowired
    private RedisGameIconService redisGameIconService;
    @Autowired
    private RedisService redisService;

    @Async("MultiExecutor")
    @Transactional
    public void SetUserFavoriteGame(List<UserFavoriteGameVO> userFavoriteGameVOList, Long userId) {
        logger.info("Setting user favorite game for user: {}", userId);
        try {
            //Delete all user favorite games
            userFavoriteGameRepository.deleteAllFavoriteGamesByUserId(userId);
            //Create new user favorite games
            for (UserFavoriteGameVO userFavoriteGameVO : userFavoriteGameVOList) {
                UsersFavoriteGameId usersFavoriteGameId = new UsersFavoriteGameId();
                usersFavoriteGameId.setUserId(userId);
                usersFavoriteGameId.setGameId(userFavoriteGameVO.getGameId());
                UsersFavoriteGame usersFavoriteGame = CreateUserFavoriteGame(usersFavoriteGameId);
                userFavoriteGameRepository.save(usersFavoriteGame);
            }
        } catch (Exception e) {
            logger.error("Error setting user favorite game: {}", e.getMessage(), e);
        }
    }

    @Transactional
    private UsersFavoriteGame CreateUserFavoriteGame(UsersFavoriteGameId usersFavoriteGameId) {
        logger.info("Creating user favorite game for user ID: {}, game ID: {}", usersFavoriteGameId.getUserId(), usersFavoriteGameId.getGameId());
        UsersFavoriteGame usersFavoriteGame = new UsersFavoriteGame();
        usersFavoriteGame.setId(usersFavoriteGameId);
        usersFavoriteGame.setFavoriteStatus(true);
        usersFavoriteGame.setCreatedAt(Instant.now());
        usersFavoriteGame.setModifiedAt(Instant.now());
        logger.info("Created user favorite game for user ID: {}, game ID: {}", usersFavoriteGameId.getUserId(), usersFavoriteGameId.getGameId());
        return usersFavoriteGame;
    }

    public List<UserFavoriteGameVO> GetUserFavoriteGames(Long userId) {
        logger.info("Getting user favorite games for user ID: {}", userId);
        try {
            String key = SAVED_GAME + ":::" + userId;
            if (redisService.MemberExists(SAVED_GAME, userId)) {
                logger.info("SAVED_GAME exists in Redis cache: {}");
            } else {
                logger.info("SAVED_GAME doesn't exist in Redis cache: {}");
                redisService.AddSet(SAVED_GAME, userId);
            }
            List<Map<Short, Object>> userFavoriteGames = userFavoriteGameRepository.findSavedGameByUserId(userId);
            List<UserFavoriteGameVO> userFavoriteGameVOList = new ArrayList<>();
            if (!userFavoriteGames.isEmpty()) {
                logger.info("User favorite games found for user ID: {}", userId);
                userFavoriteGameVOList = TransferToUserFavoriteGameVO(userFavoriteGames);
                //Save user saved game to Redis
                redisGameIconService.SetUserFavoriteGameCache(key, userId, userFavoriteGameVOList);
            } else {
                logger.info("No user favorite games found for user ID: {}", userId);
                userFavoriteGameVOList = Collections.emptyList();
                redisGameIconService.SetUserFavoriteGameCache(key, userId, userFavoriteGameVOList);
            }
            return userFavoriteGameVOList;
        } catch (Exception e) {
            logger.error("Error getting user favorite games: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private static List<UserFavoriteGameVO> TransferToUserFavoriteGameVO(List<Map<Short, Object>> userFavoriteGames) {
        logger.info("Transferring user favorite games to VO");
        try {
            List<UserFavoriteGameVO> userFavoriteGameVOList = new ArrayList<>();
            for (Map<Short, Object> map : userFavoriteGames) {
                UserFavoriteGameVO userFavoriteGameVO = new UserFavoriteGameVO();
                userFavoriteGameVO.setGameId((Short) map.get("icon_id"));
                userFavoriteGameVO.setGenreId((Byte) map.get("genre_id"));
                userFavoriteGameVO.setGameName((String) map.get("game_name"));
                userFavoriteGameVO.setIconUrl((String) map.get("icon_url"));
                userFavoriteGameVOList.add(userFavoriteGameVO);
            }
            return userFavoriteGameVOList;
        } catch (Exception e) {
            logger.error("Error transferring user favorite games to VO: {}", e.getMessage(), e);
        }
        return Collections.emptyList();
    }

}
