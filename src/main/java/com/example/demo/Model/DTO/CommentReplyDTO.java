package com.example.demo.Model.DTO;

import com.example.demo.Annotation.ValidGameId;
import com.example.demo.Annotation.ValidGenreId;
import com.example.demo.Annotation.ValidPostId;
import com.example.demo.Annotation.ValidUserId;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

@Data
public class CommentReplyDTO implements Serializable {
    @NotNull(message = "GenreId is required")
    @ValidGenreId
    private Byte genreId;
    @NotNull(message = "GameId is required")
    @ValidGameId
    private Short gameId;
    @NotNull(message = "PostId is required")
    @ValidPostId
    private Long postId;
    private Long replyId;
    private Long toReplyId;
    private Long commentId;
    @NotBlank(message = "Content is required")
    @Length(max = 4095, message = "Text length not eligible")
    private String content;
    private Long fromUid;
    private String fromUsername;
    @NotNull(message = "ToUid is required")
    @ValidUserId
    private Long toUid;
    private Integer typeId;
    private Long ipvFour;
    private String ipvSix;
    private Instant createdAt;

}
