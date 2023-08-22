package com.example.demo.Model.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "game_genres")
public class GameGenre {
    @Id
    @Column(name = "genre_id", nullable = false)
    private Byte id;

    @Size(max = 31)
    @NotNull
    @Column(name = "genre_name", nullable = false, length = 31)
    private String genreName;

}