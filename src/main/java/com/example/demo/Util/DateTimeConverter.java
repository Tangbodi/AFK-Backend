package com.example.demo.Util;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

@Component
public class DateTimeConverter {
    public static String DateTimeConvertFromString(String dateTimeString) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTimeString);
        String formattedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
        return formattedDateTime;
    }
    public static String DateTimeConvertFromInstant(Instant instant) {
        //Convert the Instant to a ZonedDateTime with a specific time zone
        //ZoneId zoneId = ZoneId.of("UTC");
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = formatter.format(zonedDateTime);
        return formattedDateTime;
    }
}
