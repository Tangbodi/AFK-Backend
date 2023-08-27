package com.example.demo.Repository;

import com.example.demo.Model.Entity.UsersFavoriteGame;
import com.example.demo.Model.Entity.UsersFavoriteGameId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserFavoriteGameRepository extends JpaRepository<UsersFavoriteGame, UsersFavoriteGameId> {

    @Query(value = "SELECT gi.icon_id, gi.genre_id, gi.game_name, gi.icon_url\n" +
            "FROM game_icons gi\n" +
            "JOIN users_favorite_games uf ON gi.icon_id = uf.game_id\n" +
            "WHERE uf.user_id = :userId\n" +
            "AND uf.favorite_status = 1;", nativeQuery = true)
    List<Map<Short, Object>> findByUserId(@Param("userId") Long userId);
}
