package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "SELECT p.post_id, p.text_render, p.title, pgm.game_id, pgm.genre_id\n" +
            "FROM afk.posts p\n" +
            "JOIN afk.posts_games_map pgm ON p.post_id = pgm.post_id\n" +
            "WHERE title LIKE %:keyword% OR text_render LIKE %:keyword%", nativeQuery = true)
    List<Map<Short, Object>> findByKeyword(@Param("keyword") String keyword);

}
