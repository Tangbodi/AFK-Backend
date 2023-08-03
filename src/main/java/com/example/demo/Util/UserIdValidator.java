package com.example.demo.Util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserIdValidator {

    public static boolean CheckUserId(String userId) {
        try{
            UUID.fromString(userId).toString();
        }catch (IllegalArgumentException exception){
            return false;
        }
        return true;
    }
}
