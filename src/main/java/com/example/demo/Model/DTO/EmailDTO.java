package com.example.demo.Model.DTO;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class EmailDTO implements Serializable {
    @NotBlank(message = "Email is required")
    @Length(max = 60, message = "Email address length not eligible")
    @Email(message = "Email is invalid")
    private String email;

    //It could be token for forgot password
    private String userId;
    private String siteURL;
}
