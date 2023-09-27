package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.MentionOfUsername;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MentionOfUsernameRepository extends JpaRepository<MentionOfUsername, Long>{
    @Modifying
    @Query(value = "UPDATE afk.mention_of_username SET mention_on = :status WHERE user_id = :userId", nativeQuery = true)
    void UpdateStatus(@Param("status") Integer status, @Param("userId") Long userId);
}
