package com.example.demo.Repository;

import com.example.demo.Model.Entity.UsersFavoritePost;
import com.example.demo.Model.Entity.UsersFavoritePostId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFavoritePostRepository extends JpaRepository<UsersFavoritePost, UsersFavoritePostId> {

}
