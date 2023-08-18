package com.example.demo.Service.Games;

import com.example.demo.Mapper.Repository.GameIconRepository;
import com.example.demo.Mapper.Repository.HomeGameImageRepository;
import com.example.demo.Model.VO.GameIconVO;
import com.example.demo.Model.VO.HomeGameImageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class GameIconService {
    private static final Logger logger = LoggerFactory.getLogger(GameIconService.class);
    @Autowired
    private GameIconRepository gameIconRepository;
    @Autowired
    private HomeGameImageRepository homeGameImageRepository;

    public List<GameIconVO> GetAllGameIcons() {
        logger.info("Getting all game icons");
        try {
            List<Map<Short, Object>> gameIconList = gameIconRepository.findAllGameIcons();
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

    private static List<GameIconVO> MapGameIconsToVOList(List<Map<Short, Object>> gameIconList) {
        logger.info("Transferring game icons to VO");
        List<GameIconVO> gameIconVOList = new ArrayList<>();
        try{
            for (Map<Short, Object> gameIcon : gameIconList) {
                GameIconVO gameIconVO = new GameIconVO();
                gameIconVO.setGenreId((Byte) gameIcon.get("genre_id"));
                gameIconVO.setGameId((Short) gameIcon.get("game_id"));
                gameIconVO.setGameName((String) gameIcon.get("game_name"));
                gameIconVO.setIconUrl((String) gameIcon.get("icon_url"));
                gameIconVOList.add(gameIconVO);
            }
        } catch (Exception e) {
            logger.error("Failed to transfer game icons to VO: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
        return gameIconVOList;
    }
    public List<HomeGameImageVO> GetHomeGameImages() {
        logger.info("Getting home game images");
        try {
            List<Map<Short, Object>> homeGameImageList = homeGameImageRepository.findHomeGameImage();
            return MapHomeGameImagesToVOList(homeGameImageList);
        } catch (Exception e) {
            logger.error("Failed to get home game images: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    private static List<HomeGameImageVO> MapHomeGameImagesToVOList(List<Map<Short, Object>> homeGameImageList){
        logger.info("Transferring home game images to VO");
        List<HomeGameImageVO> homeGameImageVOList = new ArrayList<>();
        try{
            for (Map<Short, Object> homeGameImage : homeGameImageList) {
                HomeGameImageVO homeGameImageVO = new HomeGameImageVO();
                homeGameImageVO.setImageId((Short) homeGameImage.get("image_id"));
                homeGameImageVO.setImageUrl((String) homeGameImage.get("image_url"));
                homeGameImageVOList.add(homeGameImageVO);
            }
        }catch (Exception e){
            logger.error("Failed to transfer home game images to VO: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
        return homeGameImageVOList;
    }
}

