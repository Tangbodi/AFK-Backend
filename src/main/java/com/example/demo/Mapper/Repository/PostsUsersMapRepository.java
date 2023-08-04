package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.PostsUsersMap;
import com.example.demo.Model.Entity.PostsUsersMapId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostsUsersMapRepository extends JpaRepository<PostsUsersMap, PostsUsersMapId>{
    @Query(value = "SELECT * FROM afk.posts_users_map WHERE post_id = :postId", nativeQuery = true)
    Optional<PostsUsersMap> findByPostId(@Param("postId") String postId);
}
