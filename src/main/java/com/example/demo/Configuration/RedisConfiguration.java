package com.example.demo.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfiguration {

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // Configure poolConfig settings (e.g., maxTotal, maxIdle, etc.)

        // Replace "localhost" with your actual Redis host
        return new JedisPool(poolConfig, "localhost");
    }
}
