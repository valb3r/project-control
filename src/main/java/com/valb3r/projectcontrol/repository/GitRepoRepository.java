package com.valb3r.projectcontrol.repository;

import com.valb3r.projectcontrol.domain.GitRepo;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource
public interface GitRepoRepository extends Neo4jRepository<GitRepo, Long> {
}
