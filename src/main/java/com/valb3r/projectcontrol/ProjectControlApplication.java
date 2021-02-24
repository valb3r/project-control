package com.valb3r.projectcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.neo4j.annotation.EnableNeo4jAuditing;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@EnableScheduling
@EnableConfigurationProperties
@EnableNeo4jAuditing
@EnableNeo4jRepositories
@EnableTransactionManagement
@SpringBootApplication
public class ProjectControlApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectControlApplication.class, args);
	}
}
