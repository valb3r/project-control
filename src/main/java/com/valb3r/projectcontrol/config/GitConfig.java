package com.valb3r.projectcontrol.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;

@Data
@Configuration
@ConfigurationProperties("git")
public class GitConfig {

    private String username;
    private String password;

    @NotBlank
    private String reposPath;

    public boolean hasCredentials() {
        return StringUtils.hasText(username) && StringUtils.hasText(password);
    }
}
