package com.valb3r.projectcontrol.repository;

import com.valb3r.projectcontrol.domain.stats.RemovedLines;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RepositoryRestResource
public interface RemovedLinesRepository extends Neo4jRepository<RemovedLines, Long> {

    Optional<RemovedLines> findByWeeklyIdAndFromAuthorId(long weeklyId, long fromAuthorId);
}
