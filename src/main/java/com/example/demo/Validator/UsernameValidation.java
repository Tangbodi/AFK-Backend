package com.example.demo.Validator;

import org.springframework.stereotype.Component;

@Component
public class UsernameValidation {
    public static boolean ValidUsername(String username){
        boolean hasWhitespace = username.contains(" ");
        boolean hasSpecialCharacters = !username.matches("[A-Za-z0-9 ]*");
        if (hasWhitespace || hasSpecialCharacters){
            return false;
        }else{
            return true;
        }
    }
}
