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
public class UsersFavoriteGameId implements Serializable {
    private static final long serialVersionUID = 6984699929252606365L;
    @Size(max = 36)
    @NotNull
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @NotNull
    @Column(name = "game_id", nullable = false)
    private Short gameId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UsersFavoriteGameId entity = (UsersFavoriteGameId) o;
        return Objects.equals(this.gameId, entity.gameId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, userId);
    }

}