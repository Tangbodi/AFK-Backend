package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long> {

    @Query(value = "SELECT m.comment_reply_id, m.content, m.from_uid, m.to_uid, ui.username AS from_username, m.created_at FROM afk.messages m\n" +
            "JOIN afk.users_info ui ON m.from_uid = ui.user_id\n" +
            "WHERE m.to_uid = :userId ORDER BY m.created_at DESC", nativeQuery = true)
    List<Map<Short, Object>> getMessageHistoryByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT m.message_id, pgm.game_id, pgm.genre_id, m.post_id, ifnull(m.comment_reply_id, 0) AS comment_reply_id, ifnull( m.from_uid, 0 ) AS from_uid, ifnull(ui.username,\"AFK\") AS from_username, ifnull(ui.avatar_url, '') AS from_avatar_url, m.to_uid, m.content, ifnull(m.type_id, '') AS type_id, m.created_at\n" +
            "FROM afk.messages_users_map mum\n" +
            "JOIN afk.messages m ON mum.message_id = m.message_id\n" +
            "LEFT JOIN afk.users_info ui ON m.from_uid = ui.user_id\n" +
            "JOIN afk.posts_games_map pgm ON m.post_id = pgm.post_id\n" +
            "WHERE mum.mentioned_uid = :userId AND mum.read_status = 0 \n" +
            "GROUP BY m.comment_reply_id, m.from_uid, m.type_id ORDER BY mum.message_id DESC", nativeQuery = true)
    List<Map<Short, Object>> getUnreadMessagesByUserId(@Param("userId") Long userId);
}
