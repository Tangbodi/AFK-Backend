package com.example.demo.Util;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

@Component
public class DateTimeConverter {
    public static String DateTimeConvert(String dateTimeString) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTimeString);
        String formattedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
        return formattedDateTime;
    }
}
