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
public class UpdateGameSaveController {
    private static final Logger logger = LoggerFactory.getLogger(UpdateGameSaveController.class);
    private static final String SAVED_GAME = ObjectNameEnum.SAVED_GAME_SET.getTypeName();
    @Autowired
    private RedisLikeSaveService redisLikeSaveService;
    @Autowired
    private UserFavoriteGameService userFavoriteGameService;

//    @Scheduled(fixedRate = 10000)
//    @PutMapping("/update-game-save")
//    public ResponseEntity UpdateGameSave() {
//        ApiResponse apiResponse;
//
//    }
}
