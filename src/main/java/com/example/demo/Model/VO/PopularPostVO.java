package com.example.demo.Model.VO;

import lombok.Data;

@Data
public class PopularPostVO {
    private Byte genreId;
    private Short gameId;
    private String postId;
    private String title;
    private String gameName;
}
