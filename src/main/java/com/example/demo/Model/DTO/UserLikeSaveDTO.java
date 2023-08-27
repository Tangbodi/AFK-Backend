package com.example.demo.Model.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

@Data
public class UserLikeSaveDTO implements Serializable {
    //post id, comment id, reply id
    @NotNull(message = "objectId is required")
    private Long objectId;

    //true: 1, false: 0
    @NotNull(message = "status is required")
    Integer status;

    //post: 0, comment: 1, reply: 2
    @NotNull(message = "typeId is required")
    Integer typeId;
    @NotBlank(message = "type is required")
    String type;
    private Long userId;
    private Instant createdAt;


    //like, save
//    String type;

}
