package com.example.demo.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController {

    @GetMapping("/api/v1/test")
    public ResponseEntity test(){
        return ResponseEntity.ok("Hello World");
    }
}
