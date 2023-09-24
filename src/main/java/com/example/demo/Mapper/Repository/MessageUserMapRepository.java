package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.MessagesUsersMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageUserMapRepository extends JpaRepository<MessagesUsersMap, Long> {

    @Modifying
    @Query(value = "UPDATE afk.messages_users_map\n" +
            "SET read_status = 1\n" +
            "WHERE message_id IN (:ids)", nativeQuery = true)
    void updateReadStatusByMessageId(@Param("ids") List<Long> messageIds);
}
