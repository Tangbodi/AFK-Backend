package com.example.demo.Model.Entity;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GameSubscriptionId implements Serializable {
    private static final long serialVersionUID = 4144632332609329552L;
    @Column(name = "game_id", nullable = false)
    private Integer gameId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        GameSubscriptionId entity = (GameSubscriptionId) o;
        return Objects.equals(this.gameId, entity.gameId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, userId);
    }

}