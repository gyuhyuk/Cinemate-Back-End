package com.capstone.cinemate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CinemateApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinemateApplication.class, args);
	}

}
