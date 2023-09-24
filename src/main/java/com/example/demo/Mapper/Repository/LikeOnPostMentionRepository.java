package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.LikeOnPostMention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeOnPostMentionRepository extends JpaRepository<LikeOnPostMention, Long> {
}
