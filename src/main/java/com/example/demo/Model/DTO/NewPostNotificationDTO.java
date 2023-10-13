package com.example.demo.Model.DTO;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
public class NewPostNotificationDTO implements Serializable {
    private Byte genreId;
    private Short gameId;
    private Long postId;
    private Long authorId;
    private Instant createdAt;

}
