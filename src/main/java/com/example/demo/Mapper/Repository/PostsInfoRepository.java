package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.PostsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostsInfoRepository extends JpaRepository<PostsInfo,String> {
}
