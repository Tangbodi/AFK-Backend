package com.example.demo.Model.VO;

import lombok.Data;

@Data
public class PostInfoVO {
    private String postId;
    private String title;
    private Integer view;
    private Integer comment;
    private Integer like;
    private Integer favorite;
    private String username;


}
