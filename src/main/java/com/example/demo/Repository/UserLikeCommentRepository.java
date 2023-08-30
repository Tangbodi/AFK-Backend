package com.example.demo.Repository;

import com.example.demo.Model.Entity.UsersLikeComment;
import com.example.demo.Model.Entity.UsersLikeCommentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserLikeCommentRepository extends JpaRepository<UsersLikeComment, UsersLikeCommentId> {

    @Query(value = "SELECT * FROM afk.users_like_comments WHERE like_status = 1", nativeQuery = true)
    List<UsersLikeComment> findAllByLikeStatus();
    @Query(value = "SELECT COUNT(*) AS total_like\n" +
            "FROM afk.users_like_comments\n" +
            "WHERE like_status = 1\n" +
            "AND comment_id = :commentId", nativeQuery = true)
    Map<String,Object> findTotalLike(Long commentId);
}
