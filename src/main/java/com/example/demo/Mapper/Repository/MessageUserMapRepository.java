package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.MessagesUsersMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageUserMapRepository extends JpaRepository<MessagesUsersMap, Long> {
    @Query(value = "SELECT * FROM afk.messages_users_map WHERE mentioned_uid = :mentionedUid AND read_status = 0", nativeQuery = true)
    List<MessagesUsersMap> findUnreadMessagesByMentionedUid(@Param("mentionedUid") Long mentionedUid);

}
