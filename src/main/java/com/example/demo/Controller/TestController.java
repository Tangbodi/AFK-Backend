package com.example.demo.Controller;

import com.example.demo.Mapper.Repository.PostImageRepository;
import com.example.demo.Service.Posts.PostImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Queue;

@RestController
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    @Autowired
    private PostImageRepository postImageRepository;
    @Autowired
    private PostImageService postImageService;
    @PostMapping("/getData")
    public ResponseEntity getData(@RequestParam("imageFiles") Queue<MultipartFile> imageFiles) throws IOException {
        logger.info("getData:::"+imageFiles.size());
        for (MultipartFile imageFile : imageFiles) {
            byte[] imageData = imageFile.getBytes();
            logger.info("ImageData for " + imageFile.getOriginalFilename() + ": " + imageData);
            imageFile.getContentType();
            System.out.println(imageFile.getContentType());
            // Process the imageData as needed image/jpeg
            int len = imageFile.getContentType().length();
            String imageType = imageFile.getContentType().substring(6,len);
            System.out.println(imageType);
        }
        return ResponseEntity.ok().build();
    }

}
