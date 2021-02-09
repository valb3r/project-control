package com.valb3r.projectcontrol.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.valb3r.projectcontrol.config.security.Oauth2Config;
import com.valb3r.projectcontrol.domain.AuthUser;
import com.valb3r.projectcontrol.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class LoginAuthorization {

    private final Oauth2Config oauth2Config;
    private final AuthUserRepository users;
    private final PasswordEncoder encoder;

    @Transactional
    public Optional<String> issueTokenIfAuthorized(String username, String password) {
        return users.findByLogin(username)
                .filter(it -> it.matches(password, encoder))
                .map(this::doGenerateToken);
    }

    @SneakyThrows
    private String doGenerateToken(AuthUser forUser) {
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneOffset.UTC);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
            .expirationTime(Date.from(currentTime.plus(oauth2Config.getValidity()).toInstant()))
            .issueTime(Date.from(currentTime.toInstant()))
            .subject(String.valueOf(forUser.getId()))
            .build();

        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).build();
        SignedJWT signedJWT = new SignedJWT(jwsHeader, claims);
        signedJWT.sign(new RSASSASigner(oauth2Config.getKeys().getPriv()));
        return signedJWT.serialize();
    }
}
