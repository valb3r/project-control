package com.valb3r.projectcontrol.service;

import com.valb3r.projectcontrol.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthUserManagement {

    private final AuthUserRepository users;
    private final PasswordEncoder encoder;

    @Transactional
    public boolean updatePassword(long userId, String oldPassword, String newPassword) {
        return users.findById(userId)
                .filter(it -> it.matches(oldPassword, encoder))
                .map(it -> {
                    it.setEncodedPassword(encoder.encode(newPassword));
                    return users.save(it);
                }).isPresent();
    }

    @Transactional
    public boolean updateLogin(long userId, String login, String password) {
        return users.findById(userId)
                .filter(it -> it.matches(password, encoder))
                .map(it -> {
                    it.setLogin(login);
                    return users.save(it);
                }).isPresent();
    }
}
