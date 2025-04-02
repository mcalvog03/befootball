package com.example.befootballapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"controller", "service"})
@EnableJpaRepositories(basePackages = "repository")
@EntityScan(basePackages = "model")
public class BefootballapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BefootballapiApplication.class, args);
	}

}
