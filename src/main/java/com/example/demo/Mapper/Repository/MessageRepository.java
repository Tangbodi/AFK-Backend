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
}
