package com.valb3r.projectcontrol.controller;

import com.valb3r.projectcontrol.service.AuthUserManagement;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import static com.valb3r.projectcontrol.config.security.ResourceServerJwtSecurityConfig.V1_RESOURCES;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping(value = V1_RESOURCES + "/auth-user")
@RequiredArgsConstructor
public class AuthUserController {

    private final AuthUserManagement management;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, path = "/update-password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid UpdatePasswordDto passwordDto, @Parameter(hidden = true) Authentication auth) {
        return management.updatePassword(Long.parseLong((String) auth.getPrincipal()), passwordDto.getOldPassword(), passwordDto.getNewPassword())
                ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, path = "/update-login")
    public ResponseEntity<Void> updateLogin(@RequestBody @Valid UpdateLoginDto loginDto, @Parameter(hidden = true) Authentication auth) {
        return management.updateLogin(Long.parseLong((String) auth.getPrincipal()), loginDto.getNewLogin(), loginDto.getPassword())
                ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
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
