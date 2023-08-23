package com.example.demo.Model.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Data
public class PostMixedDTO{
    private PostDTO postDTO;
    private List<MultipartFile> imageFiles;
}
