package com.example.demo.Model.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "posts_info")
public class PostsInfo {
    @Id
    @Column(name = "post_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "view", nullable = false)
    private Integer view;

    @NotNull
    @Column(name = "comment_reply", nullable = false)
    private Integer commentReply;

    @NotNull
    @Column(name = "`like`", nullable = false)
    private Integer like;

    @NotNull
    @Column(name = "save", nullable = false)
    private Integer save;

}