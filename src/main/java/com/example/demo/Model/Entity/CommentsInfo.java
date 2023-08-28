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
@Table(name = "comments_info")
public class CommentsInfo {
    @Id
    @Column(name = "comment_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "`like`", nullable = false)
    private Integer like;

}