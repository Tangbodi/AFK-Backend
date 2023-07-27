package com.example.demo.Model.Entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "games_genres_map")
public class GamesGenresMap {
    @EmbeddedId
    private GamesGenresMapId id;

    public GamesGenresMapId getId() {
        return id;
    }

    public void setId(GamesGenresMapId id) {
        this.id = id;
    }

    //TODO [JPA Buddy] generate columns from DB
}