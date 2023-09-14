package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.ReplyOnReplyMention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyOnReplyMentionRepository extends JpaRepository<ReplyOnReplyMention,Long> {
}
