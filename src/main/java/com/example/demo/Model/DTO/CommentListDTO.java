package com.example.demo.Model.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class CommentListDTO {
    private String username;
    private Date createdAt;
    private String content;
}
