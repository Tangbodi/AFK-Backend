package com.example.demo.Repository;

import com.example.demo.Model.Entity.PostsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PostInfoRepository extends JpaRepository<PostsInfo, Long> {
    @Query(value = "SELECT\n" +
            "    p.post_id,\n" +
            "    p.title,\n" +
            "    gm.game_name\n" +
            "FROM\n" +
            "    posts_info c\n" +
            "JOIN\n" +
            "    afk.posts p ON c.post_id = p.post_id\n" +
            "JOIN\n" +
            "    afk.posts_games_map pgm ON p.post_id = pgm.post_id\n" +
            "JOIN\n" +
            "    afk.games gm ON pgm.game_id = gm.game_id\n" +
            "ORDER BY\n" +
            "    (c.comment_reply + c.view) DESC\n" +
            "LIMIT\n" +
            "    3;", nativeQuery = true)
    List<Map<Short, Object>> findMostPopularPosts();

}
