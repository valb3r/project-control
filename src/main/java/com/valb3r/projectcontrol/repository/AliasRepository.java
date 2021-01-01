package com.valb3r.projectcontrol.repository;

import com.valb3r.projectcontrol.domain.Alias;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RepositoryRestResource
public interface AliasRepository extends Neo4jRepository<Alias, Long> {

    Optional<Alias> findByNameAndRepoId(String name, long repoId);

    @Query("MATCH (a:Alias)<-[:ALIAS]-(u:User)-[:ALIAS]->(o:Alias) WHERE id(a) = $aliasId RETURN a")
    List<Alias> findRelatedTo(@Param("aliasId") long aliasId);
}
