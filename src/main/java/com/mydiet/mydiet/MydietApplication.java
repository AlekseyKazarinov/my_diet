package com.mydiet.mydiet;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MydietApplication {

	public static void main(String[] args) {
		SpringApplication.run(MydietApplication.class, args);
	}

	/*@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
				.group("Swagger")
				.packagesToScan("com.mydiet.mydiet.controller")
				.build();
	}*/



}
