package com.valb3r.projectcontrol.repository;

import com.valb3r.projectcontrol.domain.AuthUser;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepository extends Neo4jRepository<AuthUser, Long> {

    Optional<AuthUser> findByLogin(String login);
}
