package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.MessagesUsersMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MessageUserMapRepository extends JpaRepository<MessagesUsersMap,Long> {
    @Query(value = "SELECT pr.reply_id, ui.username, pr.content FROM afk.messages_users_map mum \n" +
            "JOIN afk.messages m ON mum.message_id = m.message_id\n" +
            "JOIN afk.post_replies pr ON m.cr_id = pr.reply_id\n" +
            "JOIN afk.users_info ui ON m.from_uid = ui.user_id\n" +
            "WHERE mentioned_uid = :mentionedUid AND read_status = '0'", nativeQuery = true)
    List<Map<Short, Object>> findUnreadMessages(@Param("mentionedUid") String mentionedUid);

    @Query(value = "SELECT * FROM afk.messages_users_map WHERE mentioned_uid = :mentionedUid AND read_status = '0'", nativeQuery = true)
    List<MessagesUsersMap> findUnreadMessagesByMentionedUid(@Param("mentionedUid") String mentionedUid);
}
