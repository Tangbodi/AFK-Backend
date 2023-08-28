package com.example.demo.Repository;

import com.example.demo.Model.Entity.UsersLikeComment;
import com.example.demo.Model.Entity.UsersLikeCommentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLikeCommentRepository extends JpaRepository<UsersLikeComment, UsersLikeCommentId> {
}
