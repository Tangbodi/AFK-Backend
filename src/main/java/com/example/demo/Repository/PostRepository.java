package com.example.demo.Repository;

import com.example.demo.Model.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "SELECT * FROM afk.posts WHERE title LIKE %:keyword% OR text_render LIKE %:keyword%", nativeQuery = true)
    List<Post> findByKeyword(@Param("keyword") String keyword);

}
