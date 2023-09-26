package com.example.demo.Controller;

import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Model.DTO.ObjectUserDTO;
import com.example.demo.Model.VO.UserFavoriteGameVO;
import com.example.demo.Service.Redis.RedisGameIconService;
import com.example.demo.Service.Redis.RedisService;
import com.example.demo.Service.UserFavoriteGame.UserFavoriteGameService;
import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class UpdateGameSaveController {
    private static final Logger logger = LoggerFactory.getLogger(UpdateGameSaveController.class);
    private static final String SAVED_GAME = ObjectNameEnum.SAVED_GAME_SET.getTypeName();
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserFavoriteGameService userFavoriteGameService;
    @Autowired
    private RedisGameIconService redisGameIconService;

//    @Scheduled(fixedRate = 600000)
    @PutMapping("/update-game-save")
    public ResponseEntity UpdateSavedGame() throws IOException {
        ApiResponse apiResponse;
        Integer objectCode = ObjectNameEnum.GetTypeCode(SAVED_GAME);
        logger.info("objectCode: {}", objectCode);
        //get all object ids under objectName set in Redis
        Set<String> objectIds = redisService.GetAllSetMembers(SAVED_GAME);
        if (objectIds.isEmpty()) {
            logger.info("objectIds is empty");
        } else {
            for (String objectId : objectIds) {
                //userId, userFavoriteGameVOList
                Long userId = Long.valueOf(objectId);
                //HashSet key is SAVED_GAME:::userId in Redis
                List<UserFavoriteGameVO> userFavoriteGameVOList = redisGameIconService.GetUserFavoriteGameCache(SAVED_GAME + ":::" + userId.toString(), userId);
                userFavoriteGameService.SetUserFavoriteGame(userFavoriteGameVOList, userId);
                redisService.DeleteMember(SAVED_GAME + ":::" + userId.toString(), userId.toString());
                if (redisService.NumOfMembers(userId.toString()) == 0) {
                    redisService.RemoveHashSet(SAVED_GAME, userId.toString());
                    logger.info("Removed hash set: {}", SAVED_GAME);
                } else {
                    //
                }
            }
        }
        apiResponse = ApiResponse.success("Update saved game successfully");
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}
