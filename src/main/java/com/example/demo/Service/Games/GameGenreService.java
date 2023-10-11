package com.example.demo.Service.Games;

import com.example.demo.Mapper.Repository.GameGenreRepository;
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

    @Autowired
    private GameGenreRepository gameGenreRepository;

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

