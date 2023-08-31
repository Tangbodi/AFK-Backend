package com.example.demo.Util;

import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class Snowflake {
    private static final Logger logger = Logger.getLogger(Snowflake.class.getName());
    private static long dataCenterId = 1L;
    private static long machineId = 1L;
    private static long sequence = 0L;
    private static long lastTimestamp = -1L;

    public static synchronized long generateUniqueId() {
        logger.info("Snowflake:::generateUniqueId:::called");
        long timestamp = System.currentTimeMillis();

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & 0xFFF; // 12-bit sequence
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // Construct the Snowflake ID
        long snowflakeId = ((timestamp << 22) | (dataCenterId << 17) | (machineId << 12) | sequence);

        return snowflakeId;
    }

    private static long tilNextMillis(long lastTimestamp) {
        logger.info("Snowflake:::tilNextMillis:::called");
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

}
