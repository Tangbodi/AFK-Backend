package com.example.demo.Model.DTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRegisterDTO {
    @NotBlank(message = "Username is required")
    @Length(min = 1, max = 31, message = "Username must be between 1 and 31 characters long")
    private String username;
    @NotBlank(message = "Email is required")
    @Length(max = 63, message = "Email must be less than 63 characters long")
    @Email(message = "Email is invalid")
    private String email;
    @NotBlank(message = "Password is required")
    @Length(min =8, max = 63, message = "Password must be at least 8 characters long")
    private String password;
    @java.lang.Override
    public java.lang.String toString() {
        return "UserRegisterDTO{" +
                " userName='" + getUsername() + "'" +
                " email='" + getEmail() + "'" +
                " password='" + getPassword() + "'" +
                "}";
    }
}
