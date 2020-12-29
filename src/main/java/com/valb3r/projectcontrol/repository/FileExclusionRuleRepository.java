package com.valb3r.projectcontrol.repository;

import com.valb3r.projectcontrol.domain.FileExclusionRule;
import com.valb3r.projectcontrol.domain.FileInclusionRule;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryRestResource
public interface FileExclusionRuleRepository extends Neo4jRepository<FileExclusionRule, Long> {

    List<FileExclusionRule> findByRepoId(long repoId);
}
