package com.valb3r.projectcontrol.config.security;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerJwtSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String AUTHORIZATION_COOKIE = "X-Authorization";
    public static final String V1_RESOURCES = "/v1/resources";

    private final Oauth2Config oauth2Config;

    /**
     * Protects all resources with bearer-alike cookie 'X-Authorization'. Cookie is granted by administration app.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                // OPTIONS preflight is caught by 'oauth2ResourceServer'
                .requestMatcher(
                        new OrRequestMatcher(
                                new AntPathRequestMatcher(V1_RESOURCES + "/**", HttpMethod.GET.name()),
                                new AntPathRequestMatcher(V1_RESOURCES + "/**", HttpMethod.POST.name()),
                                new AntPathRequestMatcher(V1_RESOURCES + "/**", HttpMethod.PUT.name()),
                                new AntPathRequestMatcher(V1_RESOURCES + "/**", HttpMethod.PATCH.name()),
                                new AntPathRequestMatcher(V1_RESOURCES + "/**", HttpMethod.DELETE.name())
                        )
                )
                .authorizeRequests().anyRequest().authenticated().and()
                .oauth2ResourceServer()
                .bearerTokenResolver(new CookieBasedJwt())
                .jwt()
                .decoder(jwtDecoder());
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(oauth2Config.getKeys().getPub()).build();
    }

    static class CookieBasedJwt implements BearerTokenResolver {

        @Override
        public String resolve(HttpServletRequest request) {
            if (null == request.getCookies()) {
                throw noTokenExceptionSupplier();
            }

            return Arrays.stream(request.getCookies())
                    .filter(it -> AUTHORIZATION_COOKIE.equalsIgnoreCase(it.getName()))
                    .map(Cookie::getValue)
                    .filter(StringUtils::hasLength)
                    .findFirst()
                    .orElseThrow(this::noTokenExceptionSupplier);
        }

        @NotNull
        public OAuth2AuthenticationException noTokenExceptionSupplier() {
            return new OAuth2AuthenticationException(new BearerTokenError("No token cookie", HttpStatus.UNAUTHORIZED, null, null));
        }
    }
}
