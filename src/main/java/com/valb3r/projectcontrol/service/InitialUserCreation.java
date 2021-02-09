package com.valb3r.projectcontrol.service;

import com.valb3r.projectcontrol.config.InitialUser;
import com.valb3r.projectcontrol.domain.AuthUser;
import com.valb3r.projectcontrol.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class InitialUserCreation {

    private final InitialUser initialUser;
    private final TransactionOperations operations;
    private final AuthUserRepository users;
    private final PasswordEncoder encoder;

    @PostConstruct
    public void initInitialUser() {
        operations.executeWithoutResult(transactionStatus -> users.findByLogin(initialUser.getLogin())
                .orElseGet(() -> {
                    var user = new AuthUser();
                    user.setLogin(initialUser.getLogin());
                    user.setPassword(initialUser.getPassword(), encoder);
                    return users.save(user);
                }));
    }
}
