package com.example.demo.Model.DTO;
import com.example.demo.Annotation.ValidPassword;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Data
public class UserRegisterDTO {
    @NotBlank(message = "Username is required")
    @Length(min = 1, max = 30, message = "Username length not eligible")
    private String username;
    @NotBlank(message = "Email is required")
    @Length(max = 60, message = "Email address length not eligible")
    @Email(message = "Email is invalid")
    private String email;
    @NotBlank(message = "Password is required")
    @Length(min =8, max = 30, message = "Password length not eligible")
    @ValidPassword
    private String password;
    @java.lang.Override
    public java.lang.String toString() {
        return "UserRegisterDTO{" +
                " username='" + getUsername() + "'" +
                " email='" + getEmail() + "'" +
                " password='" + getPassword() + "'" +
                "}";
    }
}
