package com.example.demo.Model.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "game_genres")
public class GameGenre {
    @Id
    @Column(name = "genre_id", nullable = false)
    private Byte id;

    @Column(name = "genre_name", nullable = false, length = 31)
    private String genreName;

    public Byte getId() {
        return id;
    }

    public void setId(Byte id) {
        this.id = id;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

}