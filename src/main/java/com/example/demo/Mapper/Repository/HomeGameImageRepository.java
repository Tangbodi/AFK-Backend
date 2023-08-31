package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.HomeGameImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface HomeGameImageRepository extends JpaRepository<HomeGameImage,Short> {
    @Query(value = "SELECT image_id, image_url FROM afk.home_game_images;", nativeQuery = true)
    List<Map<Short,Object>> findHomeGameImage();
}
