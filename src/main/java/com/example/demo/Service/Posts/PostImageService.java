package com.example.demo.Service.Posts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class PostImageService {
    private static final Logger logger = LoggerFactory.getLogger(PostImageService.class);

    @Transactional
    public void SetPostImage(String postId, String imageId) {
        logger.info("Setting up PostImage: {}");

    }
}
