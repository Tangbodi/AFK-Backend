package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.SaveOnPostMention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaveOnPostMentionRepository extends JpaRepository<SaveOnPostMention, Long> {
}
