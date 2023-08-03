package com.example.demo.Model.Entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class PostsUsersMapId implements Serializable {
    private static final long serialVersionUID = -3181350167747368376L;
    @Size(max = 36)
    @NotNull
    @Column(name = "post_id", nullable = false, length = 36)
    private String postId;

    @Size(max = 36)
    @NotNull
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PostsUsersMapId entity = (PostsUsersMapId) o;
        return Objects.equals(this.postId, entity.postId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, userId);
    }

}