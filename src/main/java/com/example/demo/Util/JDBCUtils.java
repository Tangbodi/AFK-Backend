package com.example.demo.Util;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

//The purpose of this class is to provide database connection pooling functionality in Java applications
public class JDBCUtils {
    private static final Logger logger = LoggerFactory.getLogger(JDBCUtils.class);

    private static HikariDataSource dataSource;

    static {
        try {
            // Load HikariCP configuration properties from a file
            Properties properties = new Properties();
            properties.load(JDBCUtils.class.getClassLoader().getResourceAsStream("hikari.properties"));

            // Create a HikariConfig object with the loaded properties
            HikariConfig config = new HikariConfig(properties);

            // Create a HikariDataSource using the configuration
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }
}

