package com.example.demo.Controller;

import com.example.demo.Util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/getData")
    public ResponseEntity getData() {
        ApiResponse apiResponse = ApiResponse.success("Hello World");
        return ResponseEntity.ok(apiResponse);
    }

}
