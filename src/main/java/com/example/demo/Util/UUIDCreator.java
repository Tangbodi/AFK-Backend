package com.example.demo.Util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDCreator {
    public static String CreateUUID(){
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        return uuid;
    }
}
