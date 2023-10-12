package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.PostOnSavedGameMention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostOnSavedGameMentionRepository extends JpaRepository<PostOnSavedGameMention,Long> {
    @Modifying
    @Query(value = "UPDATE afk.post_on_saved_game_mentions SET mention_on = :status WHERE user_id = :userId", nativeQuery = true)
    void UpdateStatus(@Param("status") Integer status, @Param("userId") Long userId);

}
