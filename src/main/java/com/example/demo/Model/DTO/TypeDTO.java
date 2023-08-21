package com.example.demo.Model.DTO;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class TypeDTO {
    @NotBlank(message = "Type cannot be blank")
    @Length(min = 1, max = 7, message = "Invalid type")
    private String type;
}
