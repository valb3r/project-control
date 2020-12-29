package com.valb3r.projectcontrol.repository;

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
public interface WeeklyCommitStatsRepository extends Neo4jRepository<WeeklyCommitStats, Long> {

    @Query("MATCH (w:WeeklyCommitStats)-[:OF]->(a:Alias) WHERE id(a) = $aliasId AND w. w.from >= $date AND w.to < $date RETURN w")
    Optional<WeeklyCommitStats> findBy(@Param("aliasId") long aliasId, @Param("date") Instant date);
}
