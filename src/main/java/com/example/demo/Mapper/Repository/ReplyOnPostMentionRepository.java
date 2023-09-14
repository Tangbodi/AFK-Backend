package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.ReplyOnPostMention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyOnPostMentionRepository extends JpaRepository<ReplyOnPostMention,Long> {

}
