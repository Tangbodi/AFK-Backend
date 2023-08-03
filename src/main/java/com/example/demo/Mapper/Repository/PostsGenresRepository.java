package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.PostsGenresMap;
import com.example.demo.Model.Entity.PostsGenresMapId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostsGenresRepository extends JpaRepository<PostsGenresMap, PostsGenresMapId> {
}
