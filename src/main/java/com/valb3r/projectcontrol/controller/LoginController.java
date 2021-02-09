package com.valb3r.projectcontrol.controller;

import com.valb3r.projectcontrol.service.LoginAuthorization;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import static com.valb3r.projectcontrol.config.security.ResourceServerJwtSecurityConfig.AUTHORIZATION_COOKIE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping(value = "/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginAuthorization authorization;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> login(
        @RequestBody @Valid LoginDto loginDto
    ) {
        return authorization.issueTokenIfAuthorized(loginDto.getUsername(), loginDto.getPassword())
                .map(this::buildResponse)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @DeleteMapping
    public ResponseEntity<Void> logout() {
        return buildResponse("");
    }

    private ResponseEntity<Void> buildResponse(String token) {
        var cookie = ResponseCookie.from(AUTHORIZATION_COOKIE, token)
                .path("/")
                .httpOnly(true)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }


    @Data
    public static class LoginDto {

        @NotBlank
        private String username;

        @NotBlank
        private String password;
    }

}
