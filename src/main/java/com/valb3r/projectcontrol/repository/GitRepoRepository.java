package com.valb3r.projectcontrol.repository;

import com.valb3r.projectcontrol.domain.GitRepo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
