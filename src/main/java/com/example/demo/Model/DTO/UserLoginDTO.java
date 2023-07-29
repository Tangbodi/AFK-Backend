package com.example.demo.Model.DTO;

import com.example.demo.Annotation.FixedLength;
import com.example.demo.Annotation.ValidPassword;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserLoginDTO {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Password is required")
    @FixedLength(30)
    @ValidPassword
    private String password;

    @java.lang.Override
    public java.lang.String toString() {
        return "UserRegisterDTO{" +
                " username='" + getUsername() + "'" +
                " password='" + getPassword() + "'" +
                "}";
    }
}
