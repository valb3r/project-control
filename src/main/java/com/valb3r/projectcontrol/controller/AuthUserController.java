package com.valb3r.projectcontrol.controller;

import com.valb3r.projectcontrol.domain.AuthUser;
import com.valb3r.projectcontrol.repository.AuthUserRepository;
import com.valb3r.projectcontrol.service.AuthUserManagement;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import static com.valb3r.projectcontrol.config.security.ResourceServerJwtSecurityConfig.V1_RESOURCES;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.oauth2.jwt.JwtClaimNames.SUB;


@RestController
@RequestMapping(value = V1_RESOURCES + "/auth-user")
@RequiredArgsConstructor
public class AuthUserController {

    private final AuthUserRepository auths;
    private final AuthUserManagement management;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, path = "/update-password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid UpdatePasswordDto passwordDto, @Parameter(hidden = true) Authentication auth) {
        return management.updatePassword(authId(auth), passwordDto.getOldPassword(), passwordDto.getNewPassword())
                ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, path = "/update-login")
    public ResponseEntity<Void> updateLogin(@RequestBody @Valid UpdateLoginDto loginDto, @Parameter(hidden = true) Authentication auth) {
        return management.updateLogin(authId(auth), loginDto.getNewLogin(), loginDto.getPassword())
                ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping(path = "/me")
    public ResponseEntity<AuthUser> me(@Parameter(hidden = true) Authentication auth) {
        return auths.findById(authId(auth))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private long authId(Authentication auth) {
        return Long.parseLong((String) ((Jwt) auth.getPrincipal()).getClaims().get(SUB));
    }

    @Data
    public static class UpdateLoginDto {

        @NotBlank
        private String newLogin;

        @NotBlank
        private String password;
    }

    @Data
    public static class UpdatePasswordDto {

        @NotBlank
        private String oldPassword;

        @NotBlank
        private String newPassword;
    }
}
