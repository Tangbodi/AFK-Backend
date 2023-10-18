package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.PostsGamesMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PostGameMapRepository extends JpaRepository<PostsGamesMap, Long> {
    @Query(value = "WITH RankedPosts AS (\n" +
            "    SELECT post_id, game_id, genre_id, created_at, ROW_NUMBER() OVER (PARTITION BY genre_id ORDER BY created_at DESC) AS row_num\n" +
            "    FROM afk.posts_games_map\n" +
            "),\n" +
            "PostsWithGame AS (\n" +
            "    SELECT rp.post_id, rp.genre_id, rp.game_id, p.title, gm.game_name, rp.created_at\n" +
            "    FROM RankedPosts rp\n" +
            "    JOIN afk.posts p ON rp.post_id = p.post_id\n" +
            "    JOIN afk.games gm ON rp.game_id = gm.game_id\n" +
            ")\n" +
            "SELECT p.post_id, p.genre_id, p.game_id, p.title, p.game_name, p.created_at\n" +
            "FROM PostsWithGame p\n" +
            "ORDER BY created_at DESC\n" +
            "LIMIT 6;", nativeQuery = true)
    List<Map<String, Object>> findLatestPostsGamesMap();

    @Query(value = "SELECT pgm.post_id, p.title, pi.view, pi.comment_reply, pi.like, pi.save , ui.username, pgm.created_at\n" +
            "FROM afk.posts_games_map pgm \n" +
            "JOIN afk.posts_info pi ON pi.post_id = pgm.post_id \n" +
            "JOIN afk.posts p ON p.post_id = pgm.post_id\n" +
            "JOIN afk.posts_users_map pum ON pum.post_id = pgm.post_id\n" +
            "JOIN afk.users_info ui ON ui.user_id = pum.user_id\n" +
            "WHERE pgm.game_id = :gameId ORDER BY created_at DESC", nativeQuery = true)
    List<Map<String, Object>> findAllPostsInOneGame(@Param("gameId") Short gameId);

    @Query(value = "SELECT p.post_id, g.game_name, ui.user_id, ui.username, p.title,p.text_render, pi.view, pi.comment_reply, pi.like, pi.save, p.created_at\n" +
            "FROM afk.posts_games_map pgm\n" +
            "JOIN afk.posts p ON pgm.post_id = p.post_id\n" +
            "JOIN afk.posts_users_map pum ON pgm.post_id = pum.post_id \n" +
            "JOIN afk.users_info ui ON pum.user_id = ui.user_id\n" +
            "JOIN afk.posts_info pi ON pgm.post_id =pi.post_id\n" +
            "LEFT JOIN afk.games g ON pgm.game_id = g.game_id\n" +
            "WHERE pgm.genre_id = :genreId\n" +
            "AND pgm.game_id = :gameId\n" +
            "AND pgm.post_id = :postId", nativeQuery = true)
    List<Map<String, Object>> findByGenreGamePostId(@Param("genreId") Byte genreId, @Param("gameId") Short gameId, @Param("postId") Long postId);
}
