package com.example.demo.Model.DTO;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UserSettingDTO implements Serializable {
    @NotNull(message = "type cannot be blank")
    private String type;
    @NotNull(message = "status is required")
    Integer status;
    private Long userId;
}
