package com.example.demo.Controller;

import com.example.demo.Model.VO.GameIconVO;
import com.example.demo.Service.Games.GameIconService;
import com.example.demo.Service.Redis.RedisService;
import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationController.class);
    @Autowired
    private GameIconService gameIconService;
    @Autowired
    private RedisService redisService;
    @GetMapping("/all-game")
    public ResponseEntity GetAllGame() {
        if(redisService.CheckAllGameIconsCache()){
            List<GameIconVO> gameIconVOList = redisService.GetAllGameIconsCache();
            ApiResponse<List<GameIconVO>> apiResponse = ApiResponse.success(gameIconVOList);
            return ResponseEntity.ok(apiResponse);
        } else{
            List<GameIconVO> gameIconVOList = gameIconService.GetAllGameIcon();
            ApiResponse<List<GameIconVO>> apiResponse = ApiResponse.success(gameIconVOList);
            return ResponseEntity.ok(apiResponse);
        }
    }
}
