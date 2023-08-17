package com.example.demo.Util;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

@Component
public class TimestampCreator {
    private static final Logger logger = Logger.getLogger(TimestampCreator.class.getName());
    public static String CreateTimestamp(){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String timestamp = sdf.format(new Date());
            return timestamp;
        }catch (Exception e){
            logger.severe("CreateTimestamp:::Exception:::"+e);
        }
        return null;
    }
}
