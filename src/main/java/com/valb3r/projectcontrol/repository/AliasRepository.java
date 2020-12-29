package com.valb3r.projectcontrol.repository;

import com.valb3r.projectcontrol.domain.Alias;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RepositoryRestResource
public interface AliasRepository extends Neo4jRepository<Alias, Long> {

    Optional<Alias> findByNameAndRepoId(String name, long repoId);
}
