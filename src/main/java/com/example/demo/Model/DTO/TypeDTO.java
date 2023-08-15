package com.example.demo.Model.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TypeDTO {
    @NotBlank(message = "Type cannot be blank")
    private String type;
}
