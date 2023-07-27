package com.example.demo.Model.Entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "users_like_posts")
public class UsersLikePost {
    @EmbeddedId
    private UsersLikePostId id;

    @Column(name = "like_status", nullable = false)
    private Boolean likeStatus = false;

    public UsersLikePostId getId() {
        return id;
    }

    public void setId(UsersLikePostId id) {
        this.id = id;
    }

    public Boolean getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(Boolean likeStatus) {
        this.likeStatus = likeStatus;
    }

}