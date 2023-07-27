package com.example.demo.Model.VO;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserInfoVO {
    @NotBlank
    @Length(min = 1, max = 31)
    private String username;
    @NotBlank
    @Length(max = 63)
    @Email
    private String email;


    @Length(min = 11, max = 11)
    private String phone;
    @URL
    private String avatar_url;

}
