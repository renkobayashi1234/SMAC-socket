package com.example.smacsocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmacSocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmacSocketApplication.class, args);
	}

}
