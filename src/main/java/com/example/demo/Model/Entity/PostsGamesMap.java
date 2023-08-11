package com.example.demo.Model.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "posts_games_map")
public class PostsGamesMap {
    @Id
    @Size(max = 36)
    @Column(name = "post_id", nullable = false, length = 36)
    private String id;

    @NotNull
    @Column(name = "game_id", nullable = false)
    private Short gameId;

    @NotNull
    @Column(name = "genre_id", nullable = false)
    private Byte genreId;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

}