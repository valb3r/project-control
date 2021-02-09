package com.valb3r.projectcontrol.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@NodeEntity
public class AuthUser {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String login;

    @NotBlank
    @JsonIgnore
    private String encodedPassword;

    public boolean matches(String password, PasswordEncoder encoder) {
        return encoder.matches(password, encodedPassword);
    }

    public void setPassword(String password, PasswordEncoder encoder) {
        setEncodedPassword(encoder.encode(password));
    }
}
