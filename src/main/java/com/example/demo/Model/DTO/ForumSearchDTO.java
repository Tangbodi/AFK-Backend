package com.example.demo.Model.DTO;

import com.example.demo.Annotation.ValidForumKeyword;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class ForumSearchDTO {
    @NotBlank(message = "Keyword is required")
    @Length(min = 1, max = 99, message = "Keyword length not eligible")
    @ValidForumKeyword(message = "Keyword can't contain special characters or whitespace")
    private String keyword;

}
