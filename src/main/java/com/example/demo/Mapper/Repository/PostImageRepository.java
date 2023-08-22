package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage,Long> {
    @Query(value = "SELECT image_url FROM afk.post_images WHERE post_id = :postId", nativeQuery = true)
    List<Map<Short,Object>> findAllImageURLByPostId(Long postId);
}
