package com.valb3r.projectcontrol.repository;

import com.valb3r.projectcontrol.domain.stats.TotalOwnershipStats;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
@RepositoryRestResource
public interface TotalOwnershipStatsRepository extends Neo4jRepository<TotalOwnershipStats, Long> {

    @Query("MATCH (t:TotalOwnershipStats)-[r:OF]->(a:Alias) WHERE id(a) = $aliasId AND t.from = $start RETURN t")
    Optional<TotalOwnershipStats> findBy(@Param("aliasId") long aliasId, @Param("start") Instant start);
}
