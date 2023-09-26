package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.LikeOnPostMention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeOnPostMentionRepository extends JpaRepository<LikeOnPostMention, Long> {
    @Modifying
    @Query(value = "UPDATE afk.like_on_post_mentions SET mention_on = :status WHERE user_id = :userId", nativeQuery = true)
    void UpdateStatus(@Param("status") Byte status, @Param("userId") Long userId);

}
