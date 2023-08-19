package com.example.demo.Model.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "posts_info")
public class PostsInfo {
    @Id
    @Column(name = "post_id", nullable = false, length = 36)
    private String id;

    @Column(name = "view", nullable = false)
    private Integer view;

    @Column(name = "comment", nullable = false)
    private Integer comment;

    @Column(name = "`like`", nullable = false)
    private Integer like;

    @Column(name = "save", nullable = false)
    private Integer save;

    public String getPostId() {
        return id;
    }

    public void setPostId(String postId) {
        this.id = postId;
    }

    public Integer getView() {
        return view;
    }

    public void setView(Integer view) {
        this.view = view;
    }

    public Integer getComment() {
        return comment;
    }

    public void setComment(Integer comment) {
        this.comment = comment;
    }

    public Integer getLike() {
        return like;
    }

    public void setLike(Integer like) {
        this.like = like;
    }

    public Integer getSave() {
        return save;
    }

    public void setSave(Integer save) {
        this.save = save;
    }
}