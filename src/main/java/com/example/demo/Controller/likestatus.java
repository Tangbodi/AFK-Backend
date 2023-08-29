package com.example.demo.Controller;

import com.example.demo.Repository.UserLikeCommentRepository;
import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class likestatus {
    private static final Logger logger = LoggerFactory.getLogger(likestatus.class);

    @Autowired
    private UserLikeCommentRepository userLikeCommentRepository;

    @PostMapping("/getLIKE")
    public ResponseEntity getData() {

        List<Map<Short, Object>> usersLikeComment = userLikeCommentRepository.findByCommentIdAndUserId();
        ApiResponse apiResponse;
        apiResponse = ApiResponse.success(usersLikeComment);
        return ResponseEntity.ok(apiResponse);
    }
}
