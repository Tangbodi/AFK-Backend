package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, String>{

    @Query(value = "SELECT * FROM afk.news WHERE pub_date <= DATE_SUB(CURDATE(), INTERVAL 2 DAY)",nativeQuery = true)
    List<News> findNewsByPublishDate();
}
