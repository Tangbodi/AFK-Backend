package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostsRepository extends JpaRepository<Post, String> {
}
