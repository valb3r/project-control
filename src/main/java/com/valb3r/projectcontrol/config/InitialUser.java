package com.valb3r.projectcontrol.config;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@Configuration
@ConfigurationProperties(prefix = "initial-user")
public class InitialUser {

    @Length(min = 5)
    private String login;

    @Length(min = 5)
    private String password;
}
