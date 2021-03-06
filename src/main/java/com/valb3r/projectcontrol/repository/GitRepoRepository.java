package com.valb3r.projectcontrol.repository;

import com.valb3r.projectcontrol.domain.GitRepo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource
public interface GitRepoRepository extends Neo4jRepository<GitRepo, Long> {

    @Override
    @Query("MATCH (r:GitRepo)<-[:OF*0..5]-(n) WHERE id(r) = $repoId DETACH DELETE n")
    void deleteById(@Param("repoId") Long repoId);

    @RestResource(exported = false)
    @Query("MATCH (r:GitRepo)<-[:OF*0..5]-(n) WHERE (NOT n:Alias AND NOT n:User AND NOT n:GitRepo AND NOT n:FileInclusionRule AND NOT n:FileExclusionRule) AND id(r) = $repoId DETACH DELETE n")
    void deleteAnalyzedDataById(@Param("repoId") Long repoId);
}
