package com.example.demo.Service.Games;

import com.example.demo.Repository.GameGenreMapRepository;
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
    private GameGenreMapRepository gameGenreMapRepository;

    public GamesGenresMap FindGamesGenresMapById(Byte genreId, Short gameId) {
        logger.info("Finding game genre map by id: genreId = {}, gameId = {}", genreId, gameId);
        GamesGenresMap gamesGenresMap;
        try {
            GamesGenresMapId gamesGenresMapId = new GamesGenresMapId();
            gamesGenresMapId.setGenreId(genreId);
            gamesGenresMapId.setGameId(gameId);
            gamesGenresMap = gameGenreMapRepository.findById(gamesGenresMapId).orElse(null);
            if(gamesGenresMap != null) {
                logger.info("Game genre map found");
                return gamesGenresMap;
            } else {
                logger.info("No game genre map found");
                return null;
            }
        } catch (Exception e) {
            logger.error("Failed to find game genre map by id: {}", e.getMessage(), e);
        }
        return null;
    }
}
