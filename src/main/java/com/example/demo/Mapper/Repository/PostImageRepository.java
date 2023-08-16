package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage,String> {

}
