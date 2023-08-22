package com.example.demo.Model.DTO;

import lombok.Data;

import java.time.Instant;

@Data
public class IpAddressDTO {
    private Long pcrId;
    private Long ipvFour;
    private String ipvSix;
    private Instant createdAt;
}
