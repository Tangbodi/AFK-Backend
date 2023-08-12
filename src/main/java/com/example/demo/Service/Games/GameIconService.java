package com.example.demo.Service.Games;

import com.example.demo.Mapper.Repository.GameIconsRepository;
import com.example.demo.Model.Entity.GameIcon;
import com.example.demo.Model.VO.GameIconVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GameIconService {
    private static final Logger logger = LoggerFactory.getLogger(GameIconService.class);
    @Autowired
    private GameIconsRepository gameIconsRepository;


    public List<GameIconVO> GetAllGameIcons() {
        logger.info("Getting all game icons");
        try {
            List<GameIcon> gameIconList = gameIconsRepository.findAll();
            return MapGameIconsToVOList(gameIconList);
        } catch (Exception e) {
            logger.error("Failed to get all game icons: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

//    public List<Map<Short, Object>> GetGameIconsUnderOneGenre(Byte genreId) {
//        logger.info("Getting game icons under one genre: genreId = {}", genreId);
//        try {
//            return gameIconsRepository.findAllGameIconUnderOneGenre(genreId);
//        } catch (Exception e) {
//            logger.error("Failed to get game icons under one genre: {}", e.getMessage(), e);
//            return Collections.emptyList();
//        }
//    }

    private List<GameIconVO> MapGameIconsToVOList(List<GameIcon> gameIconList) {
        List<GameIconVO> gameIconVOList = new ArrayList<>();
        for (GameIcon gameIcon : gameIconList) {
            GameIconVO gameIconVO = new GameIconVO();
            gameIconVO.setGameId(gameIcon.getId());
            gameIconVO.setGameName(gameIcon.getGameName());
            gameIconVO.setGenreId(gameIcon.getGenreId());
            gameIconVO.setIconUrl(gameIcon.getIconUrl());
            gameIconVOList.add(gameIconVO);
        }
        return gameIconVOList;
    }
}

