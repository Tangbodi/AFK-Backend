package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.GameIcon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface GameIconsRepository extends JpaRepository<GameIcon, Short>{
    @Query(value = "SELECT afk.game_icons.icon_id, afk.game_icons.game_name, afk.game_icons.icon_url\n" +
            "FROM afk.game_icons\n" +
            "LEFT JOIN afk.games_genres_map ON game_icons.game_id = games_genres_map.game_id\n" +
            "WHERE afk.games_genres_map.genre_id =:genreId", nativeQuery = true)
    List<Map<Short, Object>> findAllGameIconUnderOneGenre(@Param("genreId") Byte genreId);

//    @Query(value = "SELECT * FROM afk.game_icons", nativeQuery = true)
//    List<GameIcon> findAllGameIcon();
}
