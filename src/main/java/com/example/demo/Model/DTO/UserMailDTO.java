package com.example.demo.Model.DTO;

import com.example.demo.Annotation.ValidPhone;
import com.example.demo.Annotation.ValidUserId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class UserMailDTO{
    @NotBlank(message = "UserId is required")
    @ValidUserId
    private String userId;
    private String country;
    private String state;
    private String address;
    private String city;
    private String zip;
    @ValidPhone
    private String phone;

}
