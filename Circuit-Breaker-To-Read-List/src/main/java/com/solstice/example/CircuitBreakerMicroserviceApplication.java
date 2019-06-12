package com.solstice.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@EnableCircuitBreaker
@RestController
@SpringBootApplication
public class CircuitBreakerMicroserviceApplication {

	@Bean
	public RestTemplate rest(RestTemplateBuilder builder) {
		return builder.build();
	}

	private BookService bookService;

	public CircuitBreakerMicroserviceApplication(BookService bookService) {
		this.bookService = bookService;
	}

	@RequestMapping("/to-read")
	public String toRead() {
		return bookService.readingList();
	}

	public static void main(String[] args) {
		SpringApplication.run(CircuitBreakerMicroserviceApplication.class, args);
	}

}
