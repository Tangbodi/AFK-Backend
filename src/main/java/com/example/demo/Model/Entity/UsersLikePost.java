package com.example.demo.Model.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "users_like_posts")
public class UsersLikePost {
    @EmbeddedId
    private UsersLikePostId id;

    @NotNull
    @Column(name = "like_status", nullable = false)
    private Boolean likeStatus = false;

}