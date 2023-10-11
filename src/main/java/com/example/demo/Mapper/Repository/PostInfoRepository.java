package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.PostsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PostInfoRepository extends JpaRepository<PostsInfo, Long> {
    @Query(value = "SELECT ggm.genre_id, ggm.game_id, gm.game_name, p.post_id, p.title\n" +
            " FROM posts_info c JOIN afk.posts p ON c.post_id = p.post_id\n" +
            "JOIN afk.posts_games_map pgm ON p.post_id = pgm.post_id\n" +
            "JOIN afk.games gm ON pgm.game_id = gm.game_id\n" +
            "JOIN afk.games_genres_map ggm ON pgm.game_id = ggm.game_id\n" +
            "ORDER BY (c.comment_reply + c.view) DESC LIMIT 6", nativeQuery = true)
    List<Map<Short, Object>> findMostPopularPosts();

}
