package com.example.demo.Model.Entity;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GamesGenresMapId implements Serializable {
    private static final long serialVersionUID = 7562654912679141910L;
    @Column(name = "genre_id", nullable = false)
    private Byte genreId;

    @Column(name = "game_id", nullable = false)
    private Short gameId;

    public Byte getGenreId() {
        return genreId;
    }

    public void setGenreId(Byte genreId) {
        this.genreId = genreId;
    }

    public Short getGameId() {
        return gameId;
    }

    public void setGameId(Short gameId) {
        this.gameId = gameId;
    }

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