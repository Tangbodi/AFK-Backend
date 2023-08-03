package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.PostsUsersMap;
import com.example.demo.Model.Entity.PostsUsersMapId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostsUsersMapRepository extends JpaRepository<PostsUsersMap, PostsUsersMapId>{
}
