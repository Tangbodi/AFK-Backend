package com.example.demo.Model.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "games_genres_map")
public class GamesGenresMap {
    @EmbeddedId
    private GamesGenresMapId id;

    @Column(name = "created_at")
    private Instant createdAt;

}