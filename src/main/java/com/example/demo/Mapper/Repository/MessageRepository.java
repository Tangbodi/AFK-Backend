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

    @Query(value = "SELECT m.message_id, m.comment_reply_id, m.from_uid, ui.username AS from_username, ifnull(ui.avatar_url, '') AS from_avatar_url,  m.to_uid, m.content, ifnull(m.type_id, '') AS type_id, ui.created_at \n" +
            "FROM afk.messages_users_map mum \n" +
            "JOIN afk.messages m ON mum.message_id = m.message_id\n" +
            "JOIN afk.users_info ui ON m.from_uid = ui.user_id\n" +
            "WHERE mum.mentioned_uid = :userId AND mum.read_status = 0", nativeQuery = true)
    List<Map<Short, Object>> getUnreadMessagesByUserId(@Param("userId") Long userId);
}
