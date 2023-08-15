package com.example.demo.Util;

import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class TimestampCreator {
    private static final Logger logger = Logger.getLogger(TimestampCreator.class.getName());
    public static Long CreateTimestamp(){
        try{
            logger.info("Creating Timestamp:::");
            return System.currentTimeMillis();
        }catch (Exception e){
            logger.severe("CreateTimestamp:::Exception:::"+e);
        }
        return null;
    }
}
