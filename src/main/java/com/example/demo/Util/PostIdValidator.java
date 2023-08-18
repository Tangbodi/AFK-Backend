package com.example.demo.Util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PostIdValidator {
    private static final Integer POST_LENGTH = 32;

    public static boolean CheckPostId(String postId) {
        if(postId.length()==POST_LENGTH){
            return true;
        }else{
            return false;
        }
    }
}
