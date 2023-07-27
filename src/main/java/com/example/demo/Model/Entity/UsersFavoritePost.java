package com.example.demo.Model.Entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "users_favorite_posts")
public class UsersFavoritePost {
    @EmbeddedId
    private UsersFavoritePostId id;

    @Column(name = "favorite_status", nullable = false)
    private Boolean favoriteStatus = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

    public UsersFavoritePostId getId() {
        return id;
    }

    public void setId(UsersFavoritePostId id) {
        this.id = id;
    }

    public Boolean getFavoriteStatus() {
        return favoriteStatus;
    }

    public void setFavoriteStatus(Boolean favoriteStatus) {
        this.favoriteStatus = favoriteStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

}