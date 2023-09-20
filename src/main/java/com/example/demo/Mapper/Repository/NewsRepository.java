package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface NewsRepository extends JpaRepository<News, String> {
    @Query(value = "SELECT n.news_id, gi.genre_id, n.game_id, gi.game_name, gi.icon_url, n.source, n.title, n.description, n.media_content_url, n.content, n.pub_date\n" +
            "FROM afk.news n\n" +
            "JOIN afk.game_icons gi ON n.game_id = gi.icon_id\n" +
            "WHERE YEAR(STR_TO_DATE(pub_date, '%a, %d %b %Y %H:%i:%s')) = 2023\n" +
            "AND (n.description IS NOT NULL AND n.media_content_url IS NOT NULL)\n" +
            "ORDER BY STR_TO_DATE(pub_date, '%a, %d %b %Y %H:%i:%s') DESC;", nativeQuery = true)
    List<Map<String, Object>> findAllNewsByPublishDate();

    @Modifying
    @Query(value = "DELETE FROM afk.news WHERE source = :source", nativeQuery = true)
    void deleteAllNewsBySource(@Param("source") String source);

    @Query(value = "SELECT n.news_id, gi.genre_id, n.game_id, gi.game_name, gi.icon_url,  n.source, n.title, ifnull(n.description,'') AS description, ifnull(n.media_content_url,'') AS media_content_url, n.content, n.pub_date\n" +
            "FROM afk.news n \n" +
            "JOIN afk.game_icons gi ON n.game_id = gi.icon_id\n" +
            "WHERE YEAR(STR_TO_DATE(pub_date, '%a, %d %b %Y %H:%i:%s')) = 2023\n" +
            "AND n.game_id = :gameId\n" +
            "AND (n.description IS NOT NULL AND n.media_content_url IS NOT NULL)\n" +
            "ORDER BY STR_TO_DATE(pub_date, '%a, %d %b %Y %H:%i:%s')DESC", nativeQuery = true)
    List<Map<String, Object>> findAllByGameId(@Param("gameId") Short gameId);

    @Query(value = "SELECT n.news_id, gi.game_name, gi.icon_url, n.source, n.title, n.media_content_url, n.content, n.pub_date\n" +
            " FROM afk.news n \n" +
            " LEFT JOIN afk.game_icons gi ON n.game_id = gi.icon_id\n" +
            " WHERE n.news_id = :newsId", nativeQuery = true)
    List<Map<String, Object>> findByNewsId(@Param("newsId") String newsId);
}
