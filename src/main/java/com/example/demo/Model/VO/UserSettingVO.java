package com.example.demo.Model.VO;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserSettingVO implements Serializable {
    private Integer commentOnPost;
    private Integer likeOnComment;
    private Integer likeOnPost;
    private Integer postOnSavedGame;
    private Integer replyOnComment;
    private Integer saveOnPost;
    private Integer mentionOfUsername;
}
