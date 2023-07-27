package com.example.demo.Model.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "user_avatars")
public class UserAvatar {
    @Id
    @Column(name = "avatar_id", nullable = false, length = 31)
    private String id;

    @Column(name = "user_id", nullable = false, length = 31)
    private String userId;

    @Column(name = "avatar_type", nullable = false, length = 31)
    private String avatarType;

    @Column(name = "avatar_path", nullable = false, length = 127)
    private String avatarPath;

    @Column(name = "avatar_url", nullable = false, length = 63)
    private String avatarUrl;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

    public String getAvatarId() {
        return id;
    }

    public void setAvatarId(String avatarId) {
        this.id = avatarId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatarType() {
        return avatarType;
    }

    public void setAvatarType(String avatarType) {
        this.avatarType = avatarType;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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