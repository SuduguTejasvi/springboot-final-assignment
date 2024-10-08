package com.tejasvi.trainer_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TrainerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainerServiceApplication.class, args);
	}

}
