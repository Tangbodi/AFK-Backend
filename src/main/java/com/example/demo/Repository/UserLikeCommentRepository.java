package com.example.demo.Repository;

import com.example.demo.Model.Entity.UsersLikeComment;
import com.example.demo.Model.Entity.UsersLikeCommentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserLikeCommentRepository extends JpaRepository<UsersLikeComment, UsersLikeCommentId> {
    @Query(value = "SELECT p.post_id, ui.user_id, ui.username, p.title,p.text_render, pi.view, pi.comment_reply, pi.like, pi.save, ufp.like_status, ufp.save_status, p.created_at\n" +
            "FROM afk.posts_games_map pgm\n" +
            "JOIN afk.posts p ON pgm.post_id = p.post_id\n" +
            "JOIN afk.posts_users_map pum ON pgm.post_id = pum.post_id \n" +
            "JOIN afk.users_info ui ON pum.user_id = ui.user_id\n" +
            "JOIN afk.posts_info pi ON pi.post_id = pgm.post_id\n" +
            "LEFT JOIN afk.users_favorite_posts ufp ON ufp.post_id = pgm.post_id \n" +
            "AND ufp.user_id = pum.user_id\n" +
            "WHERE pgm.genre_id = 3\n" +
            "AND ufp.user_id = 7099779735567339520\n" +
            "AND pgm.post_id = 7099783482204884992\n" +
            "AND pgm.game_id = 321", nativeQuery = true)
    List<Map<Short, Object>> findByCommentIdAndUserId();
}
