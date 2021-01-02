package com.valb3r.projectcontrol.repository;

import com.valb3r.projectcontrol.domain.stats.RemovedLines;
import com.valb3r.projectcontrol.domain.stats.WeeklyCommitStats;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
@RepositoryRestResource
public interface RemovedLinesRepository extends Neo4jRepository<RemovedLines, Long> {

    Optional<RemovedLines> findByWeeklyIdAndFromAuthorId(long weeklyId, long fromAuthorId);
}
