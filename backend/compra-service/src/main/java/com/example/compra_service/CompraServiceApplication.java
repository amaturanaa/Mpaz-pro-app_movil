package com.example.compra_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class CompraServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompraServiceApplication.class, args);
	}

	@Bean
	public WebClient webClient(@Value("${api.base-url}") String baseUrl) {
		return WebClient.builder()
			.baseUrl(baseUrl)
			.build();
	}
}
