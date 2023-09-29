package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.AfkAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AfkAnnouncementRepository extends JpaRepository<AfkAnnouncement, Long> {
    @Modifying
    @Query(value = "UPDATE afk.afk_announcements SET mention_on = :status WHERE user_id = :userId", nativeQuery = true)
    void UpdateStatus(@Param("status") Integer status, @Param("userId") Long userId);
}
