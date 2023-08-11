package com.example.demo.Model.DTO;
import com.example.demo.Annotation.ValidPassword;
import com.example.demo.Annotation.ValidUsername;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.Instant;


@Data
public class UserRegisterDTO {
    private String userId;
    @NotBlank(message = "Username is required")
    @Length(min = 1, max = 30, message = "Username length not eligible")
    @ValidUsername
    private String username;
    @NotBlank(message = "Email is required")
    @Length(max = 60, message = "Email address length not eligible")
    @Email(message = "Invalid email address")
    private String email;
    @NotBlank(message = "Password is required")
    @ValidPassword
    private String password;
    @NotBlank(message = "Confirm password is required")
    @ValidPassword
    private String confirmPassword;
    private Instant createdAt;
    @java.lang.Override
    public java.lang.String toString() {
        return "UserRegisterDTO{" +
                " userId='" + getUserId() + "'" +
                " username='" + getUsername() + "'" +
                " email='" + getEmail() + "'" +
                " password='" + getPassword() + "'" +
                " confirmPassword='" + getConfirmPassword() + "'" +
                " createdAt='" + getCreatedAt() + "'" +
                "}";
    }
}
