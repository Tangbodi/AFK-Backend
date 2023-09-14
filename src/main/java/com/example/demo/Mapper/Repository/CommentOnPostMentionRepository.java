package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.CommentOnPostMention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentOnPostMentionRepository extends JpaRepository<CommentOnPostMention,Long> {

}
