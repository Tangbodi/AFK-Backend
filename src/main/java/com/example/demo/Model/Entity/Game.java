package com.example.demo.Model.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "games")
public class Game {
    @Id
    @Column(name = "game_id", nullable = false)
    private Short id;

    @Column(name = "game_name", nullable = false, length = 127)
    private String gameName;

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

}