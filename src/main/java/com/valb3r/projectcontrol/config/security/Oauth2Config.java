package com.valb3r.projectcontrol.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

@Validated
@Data
@Configuration
@ConfigurationProperties(prefix = "oauth2")
public class Oauth2Config {

    @NotNull
    private Key keys;

    @NotNull
    private Duration validity;

    @Data
    @Validated
    public static class Key {

        @NotNull
        private RSAPublicKey pub;

        @NotNull
        private RSAPrivateKey priv;
    }
}
