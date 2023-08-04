package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<PostComment,String> {
    @Query(value = "SELECT c.post_id, c.comment_id, u.username, c.created_at, c.content\n" +
            "FROM afk.users u\n" +
            "JOIN afk.post_comments c ON u.user_id = c.from_uid\n" +
            "JOIN afk.posts p ON c.post_id = p.post_id\n" +
            "WHERE p.post_id =:postId",nativeQuery = true)
    List<Map<Short, Object>> findByPostId(@Param("postId") String postId);
}
