package com.example.demo.Service.Games;

import com.example.demo.Controller.EmailVerificationController;
import com.example.demo.Mapper.Repository.GameIconRepository;
import com.example.demo.Model.Entity.GameIcon;
import com.example.demo.Model.VO.GameIconVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GameIconService {
    private static final Logger logger = LoggerFactory.getLogger(GameIconService.class);
    @Autowired
    private GameIconRepository gameIconRepository;

    public List<GameIconVO> GetAllGameIcon() {
        logger.info("Getting all game icon");
        try{
            List<GameIcon> gameIconList = gameIconRepository.findAll();
            List<GameIconVO> gameIconVOList = new ArrayList<>();
            for(GameIcon gameIcon:gameIconList){
                GameIconVO gameIconVO = new GameIconVO();
                gameIconVO.setGameId(gameIcon.getId());
                gameIconVO.setGameName(gameIcon.getGameName());
                gameIconVO.setGameSlogan(gameIcon.getGameSlogan());
                gameIconVO.setIconUrl(gameIcon.getIconUrl());
                gameIconVOList.add(gameIconVO);
            }
            return gameIconVOList;
        }catch (Exception e){
            logger.error("Failed to get all game icon",e);
        }
        return null;
    }
    public List<Map<Short, Object>> GetGameIconUnderOneGenre(Short genreId){
        logger.info("Getting game icon under one genre");
        try{
            return gameIconRepository.findAllGameIconUnderOneGenre(genreId);
        }catch (Exception e){
            logger.error("Failed to get game icon under one genre",e);
        }
        return null;
    }
}
