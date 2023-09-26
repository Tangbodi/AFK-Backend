package com.example.demo.Model.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

@Data
public class UserLikeSaveDTO implements Serializable {

    //post id, comment id, reply id, game id
    @NotNull(message = "objectId is required")
    private Long objectId;

    //true: 1, false: 0
    @NotNull(message = "status is required")
    Integer status;

    //post_like: 0, comment_like: 1, reply_like: 2, post_save: 3, game_save: 4
    @NotNull(message = "typeId is required")
    Integer typeId;
    private Long userId;
    private Instant createdAt;

}
