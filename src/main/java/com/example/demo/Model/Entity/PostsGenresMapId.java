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
public class PostsGenresMapId implements Serializable {
    private static final long serialVersionUID = -2872265289810873630L;
    @Size(max = 36)
    @NotNull
    @Column(name = "post_id", nullable = false, length = 36)
    private String postId;

    @NotNull
    @Column(name = "genre_id", nullable = false)
    private Byte genreId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PostsGenresMapId entity = (PostsGenresMapId) o;
        return Objects.equals(this.genreId, entity.genreId) &&
                Objects.equals(this.postId, entity.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genreId, postId);
    }

}