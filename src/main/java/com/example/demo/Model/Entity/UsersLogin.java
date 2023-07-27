package com.example.demo.Model.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "users_login")
public class UsersLogin {
    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "ipv_four", columnDefinition = "INT UNSIGNED")
    private Long ipvFour;

    @Column(name = "ipv_six", length = 16)
    private String ipvSix;

    @Column(name = "login_at")
    private Instant loginAt;

    @Column(name = "logout_at")
    private Instant logoutAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getIpvFour() {
        return ipvFour;
    }

    public void setIpvFour(Long ipvFour) {
        this.ipvFour = ipvFour;
    }

    public String getIpvSix() {
        return ipvSix;
    }

    public void setIpvSix(String ipvSix) {
        this.ipvSix = ipvSix;
    }

    public Instant getLoginAt() {
        return loginAt;
    }

    public void setLoginAt(Instant loginAt) {
        this.loginAt = loginAt;
    }

    public Instant getLogoutAt() {
        return logoutAt;
    }

    public void setLogoutAt(Instant logoutAt) {
        this.logoutAt = logoutAt;
    }

}