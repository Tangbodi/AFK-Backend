package com.example.demo.Model.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "users_posts_setting")
public class UsersPostsSetting {
    @Id
    @Column(name = "user_id", nullable = false, length = 36)
    private String id;

    @Column(name = "mention_on", nullable = false)
    private Boolean mentionOn = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

    public String getUserId() {
        return id;
    }

    public void setUserId(String userId) {
        this.id = userId;
    }

    public Boolean getMentionOn() {
        return mentionOn;
    }

    public void setMentionOn(Boolean mentionOn) {
        this.mentionOn = mentionOn;
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