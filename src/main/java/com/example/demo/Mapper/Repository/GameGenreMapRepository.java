package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.GamesGenresMap;
import com.example.demo.Model.Entity.GamesGenresMapId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface GameGenreMapRepository extends JpaRepository<GamesGenresMap, GamesGenresMapId> {

    @Query(value = "SELECT * FROM games_genres_map WHERE game_id =:gameId", nativeQuery = true)
    GamesGenresMap findByGameId(@Param("gameId") Short gameId);
}
