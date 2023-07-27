package com.example.demo.Model.Entity;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UsersFavoritePostId implements Serializable {
    private static final long serialVersionUID = -8943037367897689524L;
    @Column(name = "post_id", nullable = false, length = 36)
    private String postId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UsersFavoritePostId entity = (UsersFavoritePostId) o;
        return Objects.equals(this.postId, entity.postId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, userId);
    }

}