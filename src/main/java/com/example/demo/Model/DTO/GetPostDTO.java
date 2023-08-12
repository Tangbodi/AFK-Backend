package com.example.demo.Model.DTO;

import lombok.Data;

@Data
public class GetPostDTO {
    private String postId;
    private Short gameId;
    private Byte genreId;
}
