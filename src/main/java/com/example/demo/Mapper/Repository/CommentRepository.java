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
    @Query(value = "SELECT p.post_id, pc.comment_id, ui.username, ui.avatar_url, pc.content, pc.created_at FROM afk.posts p\n" +
            "JOIN afk.post_comments pc ON p.post_id = pc.post_id\n" +
            "JOIN afk.users_info ui ON pc.from_uid = ui.user_id\n" +
            "WHERE p.post_id = :postId ORDER BY pc.created_at ASC", nativeQuery = true)
    List<Map<Short, Object>> findCommentsByPostId(@Param("postId") String postId);


    @Query(value = "WITH RankedComments AS (\n" +
            "SELECT post_id, content, created_at, ROW_NUMBER() OVER (ORDER BY created_at DESC) AS row_num\n" +
            "FROM afk.post_comments),\n" +
            "PostsWithGame AS (\n" +
            "SELECT rc.post_id, gm.game_name, rc.content, rc.created_at\n" +
            "FROM RankedComments rc\n" +
            "JOIN afk.posts_games_map pgm ON rc.post_id = pgm.post_id\n" +
            "JOIN afk.games gm ON pgm.game_id = gm.game_id)\n" +
            "SELECT p.post_id, p.content, p.game_name, p.created_at FROM PostsWithGame p ORDER BY created_at DESC\n" +
            "LIMIT 6;", nativeQuery = true)
    List<Map<Short, Object>> findNewestComments();
}
