package com.example.demo.Repository;

import com.example.demo.Model.Entity.RepliesInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyInfoRepository extends JpaRepository<RepliesInfo,Long> {
}
