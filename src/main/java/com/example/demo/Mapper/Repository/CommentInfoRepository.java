package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.CommentsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentInfoRepository extends JpaRepository<CommentsInfo,Long> {
}
