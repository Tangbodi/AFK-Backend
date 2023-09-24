package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.LikeOnCommentMention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeOnCommentMentionRepository extends JpaRepository<LikeOnCommentMention, Long> {
}
