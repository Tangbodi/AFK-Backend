package com.example.demo.Model.DTO;

import com.example.demo.Annotation.ValidPassword;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdatePasswordDTO {
    private Long userId;

    private String oldPassword;
    @NotBlank(message = "New password is required")
    @ValidPassword
    private String newPassword;
    @NotBlank(message = "Confirm password is required")
    @ValidPassword
    private String confirmPassword;
}
