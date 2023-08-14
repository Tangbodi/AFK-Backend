package com.example.demo.Model.Entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class GamesGenresMapId implements Serializable {
    private static final long serialVersionUID = 6017423013749967015L;
    @NotNull
    @Column(name = "genre_id", nullable = false)
    private Byte genreId;

    @NotNull
    @Column(name = "game_id", nullable = false)
    private Short gameId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        GamesGenresMapId entity = (GamesGenresMapId) o;
        return Objects.equals(this.genreId, entity.genreId) &&
                Objects.equals(this.gameId, entity.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genreId, gameId);
    }

}