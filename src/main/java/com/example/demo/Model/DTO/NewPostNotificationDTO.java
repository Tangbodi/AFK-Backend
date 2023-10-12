package com.example.demo.Model.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class NewPostNotificationDTO implements Serializable {
    private Byte genreId;
    private Short gameId;
    private Long postId;

}
