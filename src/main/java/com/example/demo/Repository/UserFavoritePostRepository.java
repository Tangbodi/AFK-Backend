package com.example.demo.Repository;

import com.example.demo.Model.Entity.UsersFavoritePost;
import com.example.demo.Model.Entity.UsersFavoritePostId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserFavoritePostRepository extends JpaRepository<UsersFavoritePost, UsersFavoritePostId> {

    @Query(value = "SELECT * FROM afk.users_favorite_posts WHERE like_status = 1", nativeQuery = true)
    List<UsersFavoritePost> findAllByLikeStatus();

    @Query(value = "SELECT * FROM afk.users_favorite_posts WHERE save_status = 1", nativeQuery = true)
    List<UsersFavoritePost> findAllBySaveStatus();
    @Query(value = "SELECT COUNT(*) AS total_like\n" +
            "FROM afk.users_favorite_posts\n" +
            "WHERE like_status = 1\n" +
            "AND post_id = :postId", nativeQuery = true)
    Map<String,Object> findTotalLike(@Param("postId") Long postId);

    @Query(value = "SELECT COUNT(*) AS total_save\n" +
            "FROM afk.users_favorite_posts\n" +
            "WHERE save_status = 1\n" +
            "AND post_id = :postId", nativeQuery = true)
    Map<String,Object> findTotalSave(@Param("postId") Long postId);
}
