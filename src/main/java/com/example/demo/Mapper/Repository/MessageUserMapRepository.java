package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.MessagesUsersMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageUserMapRepository extends JpaRepository<MessagesUsersMap, Long> {
//    @Query(value = "SELECT m.comment_reply_id, m.from_uid, ui.username AS from_username, m.to_uid, m.content " +
//            "FROM afk.messages_users_map mum\n" +
//            "JOIN afk.messages m ON mum.message_id = m.message_id\n" +
//            "JOIN afk.users_info ui ON m.from_uid = ui.user_id\n" +
//            "WHERE mentioned_uid = :mentionedUid", nativeQuery = true)
//    List<Map<Short, Object>> findUnreadMessages(@Param("mentionedUid") Long mentionedUid);

    @Query(value = "SELECT * FROM afk.messages_users_map WHERE mentioned_uid = :mentionedUid AND read_status = 0", nativeQuery = true)
    List<MessagesUsersMap> findUnreadMessagesByMentionedUid(@Param("mentionedUid") Long mentionedUid);

}
