package com.test.hackernews.api.hnw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.test.hackernews.api.hnw")
public class TestHackerNewsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestHackerNewsApiApplication.class, args);
	}

}
