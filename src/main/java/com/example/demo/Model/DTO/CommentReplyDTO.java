package com.example.demo.Model.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
public class CommentReplyDTO {
    @NotNull(message = "GenreId is required")
    private Byte genreId;
    @NotNull(message = "GameId is required")
    private Short gameId;
    @NotBlank(message = "PostId is required")
    private String postId;
    private String replyId;
    private String toReplyId;
    private String commentId;
    @NotBlank(message = "Content is required")
    private String content;
    private String fromUid;
    @NotBlank(message = "ToUid is required")
    private String toUid;
    private Long ipvFour;
    private String ipvSix;
    private Instant createdAt;

}
