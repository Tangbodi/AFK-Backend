package com.example.demo.Mapper.Repository;

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

    @Query(value = "SELECT COUNT(*) AS total_like\n" +
            "FROM afk.users_favorite_posts\n" +
            "WHERE like_status = 1\n" +
            "AND post_id = :postId", nativeQuery = true)
    Map<String,Object> findPostTotalLikeByLikeStatus(@Param("postId") Long postId);

    @Query(value = "SELECT COUNT(*) AS total_save\n" +
            "FROM afk.users_favorite_posts\n" +
            "WHERE save_status = 1\n" +
            "AND post_id = :postId", nativeQuery = true)
    Map<String,Object> findPostTotalLikeBySaveStatus(@Param("postId") Long postId);

    @Query(value = "SELECT p.post_id, p.title, pi.view, pi.comment_reply, pi.like, p.created_at\n" +
            "FROM afk.users_favorite_posts ufp\n" +
            "LEFT JOIN afk.posts p ON ufp.post_id = p.post_id\n" +
            "LEFT JOIN afk.posts_info pi ON p.post_id = pi.post_id \n" +
            "WHERE user_id = :user_id AND save_status = 1", nativeQuery = true)
    List<Map<String, Object>> findAllSavedPostsByUserId(@Param("user_id") Long user_id);
}
