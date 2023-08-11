package com.example.demo.Model.DTO;

import com.example.demo.Annotation.FixedLength;
import com.example.demo.Annotation.ValidPassword;
import com.example.demo.Annotation.ValidUsername;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class UserLoginDTO {
    @NotBlank(message = "Username is required")
    @Length(min = 1, max = 30, message = "Username length not eligible")
    @ValidUsername(message = "User not found")
    private String username;
    @NotBlank(message = "Password is required")
    @Length(min =8, max = 30, message = "Password length not eligible")
    @ValidPassword(message = "Username or password is incorrect")
    private String password;

    @java.lang.Override
    public java.lang.String toString() {
        return "UserRegisterDTO{" +
                " username='" + getUsername() + "'" +
                " password='" + getPassword() + "'" +
                "}";
    }
}
