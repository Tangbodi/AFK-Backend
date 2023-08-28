package com.example.demo.Repository;

import com.example.demo.Model.Entity.UsersLikeReply;
import com.example.demo.Model.Entity.UsersLikeReplyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLikeReplyRepository extends JpaRepository<UsersLikeReply, UsersLikeReplyId> {
}
