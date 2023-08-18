package com.example.demo.Service.Games;

import com.example.demo.Mapper.Repository.GameRepository;
import com.example.demo.Model.Entity.Game;
import com.example.demo.Model.VO.GameVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//@Service
//public class GameService {
//    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
//    @Autowired
//    private GameRepository gameRepository;
//    public GameVO GetGameById(Short gameId) {
//        logger.info("Getting game by id: gameId = {}", gameId);
//        try {
//            Game game = gameRepository.findById(gameId).orElse(null);
//            if(game == null) {
//                logger.info("Game not found with id: {}", gameId);
//                return null;
//            }else{
//                GameVO gameVO = new GameVO();
//                gameVO.setGameId(game.getId());
//                return gameVO;
//            }
//
//        } catch (Exception e) {
//            logger.error("Failed to get game by id: {}", e.getMessage(), e);
//            return null;
//        }
//    }
//
//}
