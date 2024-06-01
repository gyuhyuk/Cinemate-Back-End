package com.capstone.cinemate;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@EnableJpaAuditing
@SpringBootApplication
public class CinemateApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinemateApplication.class, args);
	}

	@PostConstruct
	void started(){
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
}
