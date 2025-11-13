package com.example.repartir_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Pour activer le programmé
public class RepartirBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RepartirBackendApplication.class, args);
	}

}
