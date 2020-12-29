package com.valb3r.projectcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ProjectControlApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectControlApplication.class, args);
	}
}
