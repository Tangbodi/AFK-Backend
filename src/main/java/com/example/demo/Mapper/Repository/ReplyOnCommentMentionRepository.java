package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.ReplyOnCommentMention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyOnCommentMentionRepository extends JpaRepository<ReplyOnCommentMention,Long> {
}
