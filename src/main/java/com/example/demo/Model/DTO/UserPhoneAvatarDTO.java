package com.example.demo.Model.DTO;

import com.example.demo.Annotation.FixedLength;
import lombok.Data;

@Data
public class UserPhoneAvatarDTO {
    @FixedLength(11)
    private String phone;
    private String avatarUrl;

}
