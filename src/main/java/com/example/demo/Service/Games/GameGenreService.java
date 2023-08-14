package com.example.demo.Service.Games;

import com.example.demo.Mapper.Repository.GameGenresRepository;
import com.example.demo.Model.Entity.GameGenre;
import com.example.demo.Model.VO.GameGenreVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GameGenreService {
    private static final Logger logger = LoggerFactory.getLogger(GameGenreService.class);

    private final GameGenresRepository gameGenresRepository;

    @Autowired
    public GameGenreService(GameGenresRepository gameGenresRepository) {
        this.gameGenresRepository = gameGenresRepository;
    }

    public List<GameGenreVO> GetAllGameGenres() {
        logger.info("Getting all game genres");
        try {
            List<GameGenre> gameGenreList = gameGenresRepository.findAll();
            return MapGameGenresToVOList(gameGenreList);
        } catch (Exception e) {
            logger.error("Failed to get all game genres: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private static List<GameGenreVO> MapGameGenresToVOList(List<GameGenre> gameGenreList) {
        List<GameGenreVO> gameGenreVOList = new ArrayList<>();
        for (GameGenre gameGenre : gameGenreList) {
            GameGenreVO gameGenreVO = new GameGenreVO();
            gameGenreVO.setId(gameGenre.getId());
            gameGenreVO.setGenreName(gameGenre.getGenreName());
            gameGenreVOList.add(gameGenreVO);
        }
        return gameGenreVOList;
    }
}

