package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.UsersFavoriteGame;
import com.example.demo.Model.Entity.UsersFavoriteGameId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserFavoriteGameRepository extends JpaRepository<UsersFavoriteGame, UsersFavoriteGameId> {

    @Query(value = "SELECT gi.icon_id, gi.genre_id, gi.game_name, gi.icon_url\n" +
            "FROM afk.game_icons gi\n" +
            "JOIN users_favorite_games uf ON gi.icon_id = uf.game_id\n" +
            "WHERE uf.user_id = :userId", nativeQuery = true)
    List<Map<Short, Object>> findSavedGameByUserId(@Param("userId") Long userId);
    @Modifying
    @Query(value = "DELETE FROM afk.users_favorite_games WHERE user_id = :userId", nativeQuery = true)
    void deleteAllFavoriteGamesByUserId(@Param("userId") Long userId);

}
