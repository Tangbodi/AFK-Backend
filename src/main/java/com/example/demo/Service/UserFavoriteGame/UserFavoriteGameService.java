package com.example.demo.Service.UserFavoriteGame;

import com.example.demo.Mapper.Repository.UserFavoriteGameRepository;
import com.example.demo.Model.DTO.GameGenreMapIdDTO;
import com.example.demo.Model.DTO.ObjectUserDTO;
import com.example.demo.Model.Entity.UsersFavoriteGame;
import com.example.demo.Model.Entity.UsersFavoriteGameId;
import com.example.demo.Model.VO.UserFavoriteGameVO;
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
    @Autowired
    private UserFavoriteGameRepository userFavoriteGameRepository;
    @Async("MultiExecutor")
    @Transactional
    public void SetUserFavoriteGame(List<ObjectUserDTO> objectUserDTOList) {
        logger.info("Setting user favorite game for game ID: {}", objectUserDTOList.get(0).getObjectId());
        try {
            for(ObjectUserDTO objectUserDTO : objectUserDTOList){
                UsersFavoriteGameId usersFavoriteGameId = new UsersFavoriteGameId();
                usersFavoriteGameId.setUserId(objectUserDTO.getUserId());
                usersFavoriteGameId.setGameId(objectUserDTO.getObjectId().shortValue());
                UsersFavoriteGame usersFavoriteGame = userFavoriteGameRepository.findById(usersFavoriteGameId)
                        .orElseGet(() -> CreateUserFavoriteGame(usersFavoriteGameId));
                usersFavoriteGame.setFavoriteStatus(objectUserDTO.getStatus() == 1);
                usersFavoriteGame.setModifiedAt(Instant.now());
                userFavoriteGameRepository.save(usersFavoriteGame);
                logger.info("User favorite game saved successfully for user ID: {}, game ID: {}", objectUserDTO.getUserId(), objectUserDTO.getObjectId());
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
        usersFavoriteGame.setCreatedAt(Instant.now());
        usersFavoriteGame.setModifiedAt(Instant.now());
        logger.info("Created user favorite game for user ID: {}, game ID: {}", usersFavoriteGameId.getUserId(), usersFavoriteGameId.getGameId());
        return usersFavoriteGame;
    }
    public List<UserFavoriteGameVO> GetUserFavoriteGames(Long userId) {
        logger.info("Getting user favorite games for user ID: {}", userId);
        try {
            List<Map<Short, Object>> userFavoriteGames = userFavoriteGameRepository.findByUserId(userId);
            if (!userFavoriteGames.isEmpty()) {
                logger.info("User favorite games found for user ID: {}", userId);
                return TransferToUserFavoriteGameVO(userFavoriteGames);
            } else {
                logger.info("No user favorite games found for user ID: {}", userId);
                return Collections.emptyList();
            }

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
