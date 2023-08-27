package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableAsync
@Configuration
@ComponentScan({"com.example.demo","Controller","Service","Util","Configuration"})
@SpringBootApplication
public class AfkApplication {
	public static void main(String[] args) {
		SpringApplication.run(AfkApplication.class, args);
	}

}


