package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.PostsGamesMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PostsGamesMapRepository extends JpaRepository<PostsGamesMap, String> {
    @Query(value = "WITH RankedPosts AS (\n" +
            "    SELECT\n" +
            "        post_id,\n" +
            "        game_id,\n" +
            "        genre_id,\n" +
            "        created_at,\n" +
            "        ROW_NUMBER() OVER (PARTITION BY genre_id ORDER BY created_at DESC) AS row_num  \n" +
            "    FROM\n" +
            "        afk.posts_games_map \n" +
            "),\n" +
            "PostsWithGame AS (\n" +
            "    SELECT\n" +
            "        rp.post_id,\n" +
            "        p.title,\n" +
            "        gm.game_name\n" +
            "    FROM\n" +
            "        RankedPosts rp\n" +
            "    JOIN\n" +
            "        afk.posts p ON rp.post_id = p.post_id\n" +
            "    JOIN\n" +
            "        afk.games gm ON rp.game_id = gm.game_id\n" +
            "        WHERE row_num =1\n" +
            ")\n" +
            "SELECT\n" +
            "    p.post_id,\n" +
            "    p.title,\n" +
            "    p.game_name\n" +
            "FROM\n" +
            "    PostsWithGame p;", nativeQuery = true)
    List<Map<Short, Object>> findLatestPostsGamesMap();

    @Query(value = "SELECT pgm.post_id, p.title, pi.view, pi.comment, pi.like, pi.favorite , ui.username\n" +
            "FROM afk.posts_games_map pgm \n" +
            "JOIN afk.posts_info pi ON pi.post_id = pgm.post_id \n" +
            "JOIN afk.posts p ON p.post_id = pgm.post_id\n" +
            "JOIN afk.posts_users_map pum ON pum.post_id = pgm.post_id\n" +
            "JOIN afk.users_info ui ON ui.user_id = pum.user_id\n" +
            "WHERE pgm.game_id =:gameId",nativeQuery = true)
    List<Map<Short, Object>> findAllPostInfoWithOneGame(@Param("gameId") Short gameId);
}
