package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommentRepository extends JpaRepository<PostComment, String> {
    @Query(value = "SELECT c.post_id, c.comment_id, u.username, c.created_at, c.content\n" +
            "FROM afk.users u\n" +
            "JOIN afk.post_comments c ON u.user_id = c.from_uid\n" +
            "JOIN afk.posts p ON c.post_id = p.post_id\n" +
            "WHERE p.post_id =:postId", nativeQuery = true)
    List<Map<Short, Object>> findByPostId(@Param("postId") String postId);


    @Query(value = "WITH RankedComments AS (" +
            "SELECT post_id, content, created_at, ROW_NUMBER() OVER (ORDER BY created_at DESC) AS row_num\n" +
            "FROM afk.post_comments),\n" +
            "PostsWithGame AS (\n" +
            "SELECT rc.post_id, gm.game_name, rc.content\n" +
            "FROM RankedComments rc\n" +
            "JOIN afk.posts_games_map pgm ON rc.post_id = pgm.post_id\n" +
            "JOIN afk.games gm ON pgm.game_id = gm.game_id)\n" +
            "SELECT p.post_id, p.content, p.game_name FROM PostsWithGame p\n" +
            "LIMIT 3;", nativeQuery = true)
    List<Map<Short, Object>> findNewestComments();
}
