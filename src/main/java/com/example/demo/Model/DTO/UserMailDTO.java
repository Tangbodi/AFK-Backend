package com.example.demo.Model.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserMailDTO implements Serializable {
    private String country;
    private String state;
    private String address;
    private String city;
    private String zip;

}
