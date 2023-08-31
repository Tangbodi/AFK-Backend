package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, String>{

    @Query(value = "SELECT *\n" +
            "FROM afk.news\n" +
            "WHERE STR_TO_DATE(pub_date, '%a, %d %b %Y %H:%i:%s') >= DATE_SUB(CURDATE(), INTERVAL 3 DAY)\n" +
            "ORDER BY STR_TO_DATE(pub_date, '%a, %d %b %Y %H:%i:%s') DESC",nativeQuery = true)
    List<News> findNewsByPublishDate();
}
