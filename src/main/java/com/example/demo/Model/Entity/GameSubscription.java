package com.example.demo.Model.Entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "game_subscriptions")
public class GameSubscription {
    @EmbeddedId
    private GameSubscriptionId id;

    @Column(name = "subscription_status", nullable = false)
    private Boolean subscriptionStatus = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt;

    public GameSubscriptionId getId() {
        return id;
    }

    public void setId(GameSubscriptionId id) {
        this.id = id;
    }

    public Boolean getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(Boolean subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

}