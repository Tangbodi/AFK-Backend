package com.example.demo.Service.Games;

import com.example.demo.Mapper.Repository.GameIconsRepository;
import com.example.demo.Model.Entity.GameIcon;
import com.example.demo.Model.VO.GameIconVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GameIconService {
    private static final Logger logger = LoggerFactory.getLogger(GameIconService.class);
    @Autowired
    private GameIconsRepository gameIconsRepository;

    public List<GameIconVO> GetAllGameIcon() {
        logger.info("Getting all game icon: {}");
        try{
            List<GameIcon> gameIconList = gameIconsRepository.findAll();
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
            logger.error("Failed to get all game icon: {}",e);
        }
        return null;
    }
    public List<Map<Short, Object>> GetGameIconUnderOneGenre(Byte genreId){
        logger.info("Getting game icon under one genre: {}");
        try{
            return gameIconsRepository.findAllGameIconUnderOneGenre(genreId);
        }catch (Exception e){
            logger.error("Failed to get game icon under one genre: {}",e.getMessage(),e);
        }
        return null;
    }
}
