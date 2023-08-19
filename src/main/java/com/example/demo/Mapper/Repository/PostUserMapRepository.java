package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.PostsUsersMap;
import com.example.demo.Model.Entity.PostsUsersMapId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PostUserMapRepository extends JpaRepository<PostsUsersMap, PostsUsersMapId>{
    @Query(value = "SELECT * FROM afk.posts_users_map WHERE post_id = :postId", nativeQuery = true)
    Optional<PostsUsersMap> findByPostId(@Param("postId") String postId);

    @Query(value = "SELECT pi.post_id, p.title, ui.user_id, ui.username, pi.view, pi.comment, pi.like, pi.save, pum.created_at FROM afk.posts_users_map pum\n" +
            "JOIN afk.posts_info pi ON pum.post_id = pi.post_id\n" +
            "JOIN afk.posts p ON pum.post_id = p.post_id\n" +
            "JOIN afk.users_info ui ON pum.user_id = ui.user_id\n" +
            "WHERE pum.user_id = :userId",nativeQuery = true)
    List<Map<Short, Object>> findPostUserMapByUserId(@Param("userId") String userId);
}
