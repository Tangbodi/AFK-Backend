package com.example.demo.Controller;

import com.example.demo.Constant.Enum.ObjectNameEnum;
import com.example.demo.Model.DTO.ObjectUserDTO;
import com.example.demo.Service.Redis.RedisLikeSaveService;
import com.example.demo.Service.UserFavoriteGame.UserFavoriteGameService;
import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class GameSaveController {
    private static final Logger logger = LoggerFactory.getLogger(GameSaveController.class);
    private static final String GAME_SAVE = ObjectNameEnum.GAME_SAVE_SET.getTypeName();
    @Autowired
    private RedisLikeSaveService redisLikeSaveService;
    @Autowired
    private UserFavoriteGameService userFavoriteGameService;

    @Scheduled(fixedRate = 5000)
    @PutMapping("/update-game-save")
    public ResponseEntity UpdateLikeSaveStatusAndCount() {
        ApiResponse apiResponse;

        Integer objectCode = ObjectNameEnum.GetTypeCode(GAME_SAVE);
        logger.info("objectCode: {}", objectCode);
        //get all object ids under objectName set in Redis
        Set<String> objectIds = redisLikeSaveService.GetAllSetMembers(GAME_SAVE);
        if (objectIds.isEmpty()) {
            logger.info("objectIds is empty");

        } else {
            logger.info("objectIds: {}", objectIds);
            List<ObjectUserDTO> objectUserDTOList = new ArrayList<>();
            for (String objectId : objectIds) {
                //userId,date
                //HashSet key is post_like:::postId in Redis
                Map<String, String> hashSetMap = redisLikeSaveService.GetHashValue(GAME_SAVE + ":::" + objectId);
                hashSetMap.entrySet().stream().forEach(entry -> {
                    ObjectUserDTO objectUserDTO = new ObjectUserDTO();
                    objectUserDTO.setObjectId(Long.valueOf(objectId));
                    String userId = entry.getKey();
                    objectUserDTO.setUserId(Long.valueOf(userId));
                    objectUserDTO.setStatus(Integer.valueOf(entry.getValue()));
                    objectUserDTOList.add(objectUserDTO);
                    redisLikeSaveService.DeleteMember(GAME_SAVE + ":::" + objectId, userId);
                    if (redisLikeSaveService.NumOfMembers(objectId) == 0) {
                        redisLikeSaveService.RemoveHashSet(GAME_SAVE, objectId);
                    } else {
                        //
                    }
                });
            }
            userFavoriteGameService.SetUserFavoriteGame(objectUserDTOList);
        }
        apiResponse = ApiResponse.success("Updated game save status successfully");
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}
