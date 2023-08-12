package com.example.demo.Service.Games;

import com.example.demo.Mapper.Repository.GamesGenresMapRepository;
import com.example.demo.Model.DTO.GameGenreMapIdDTO;
import com.example.demo.Model.Entity.GamesGenresMap;
import com.example.demo.Model.Entity.GamesGenresMapId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameGenreMapService {
    private static final Logger logger = LoggerFactory.getLogger(GameGenreMapService.class);
    @Autowired
    private GamesGenresMapRepository gamesGenresMapRepository;
    public GamesGenresMap FindGamesGenresMapById(GameGenreMapIdDTO gameGenreMapIdDTO){
        logger.info("Finding game genre map by id: genreId = {}, gameId = {}", gameGenreMapIdDTO.getGenreId(), gameGenreMapIdDTO.getGameId());
        try{
            GamesGenresMap gamesGenresMap = new GamesGenresMap();
            GamesGenresMapId gamesGenresMapId = new GamesGenresMapId();
            gamesGenresMapId.setGenreId(gameGenreMapIdDTO.getGenreId());
            gamesGenresMapId.setGameId(gameGenreMapIdDTO.getGameId());
            return gamesGenresMapRepository.findById(gamesGenresMapId).orElse(null);
        } catch (Exception e){
            logger.error("Failed to find game genre map by id: {}", e.getMessage(), e);
            return null;
        }

    }
}
