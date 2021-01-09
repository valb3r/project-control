package com.valb3r.projectcontrol.repository;

import com.valb3r.projectcontrol.domain.User;
import com.valb3r.projectcontrol.domain.dto.RemovedLinesWeeklyStats;
import com.valb3r.projectcontrol.domain.dto.WeeklyOwnershipStats;
import com.valb3r.projectcontrol.domain.dto.WeeklyWorkStats;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
@RepositoryRestResource(path = "statistics")
public interface StatisticsRepository extends org.springframework.data.repository.Repository<User, Long> {

    // Note - Spring Data REST handles `Iterable` result from query incorrectly - assumes it is CollectionModel
    @Query("MATCH (u:User)-[:ALIAS]->(a:Alias)-[:OF]->(r:GitRepo) WHERE id(r) = $repoId AND id(u) = $userId " +
            "WITH a,r MATCH (a)<-[:OF]-(w:WeeklyCommitStats)-[:OF]->(r:GitRepo) WHERE w.from >= $from AND w.to <= $to " +
            "RETURN w.from AS from, w.to AS to, SUM(w.commitCount) AS totalCommits, sum(w.linesAdded) AS linesAdded, sum(w.linesRemoved) AS linesRemoved ORDER BY w.from")
    WeeklyWorkStats[] getWeeklyWorkStats(@Param("repoId") Long repoId, @Param("userId") Long userId, @Param("from") Instant from, @Param("to") Instant to);

    // Note - Spring Data REST handles `Iterable` result from query incorrectly - assumes it is CollectionModel
    @Query("MATCH (u:User)-[:ALIAS]->(a:Alias)-[:OF]->(r:GitRepo) WHERE id(r) = $repoId AND id(u) = $userId " +
            "WITH a,r MATCH (a)<-[:OF]-(w:TotalOwnershipStats)-[:OF]->(r:GitRepo) WHERE w.from >= $from AND w.to <= $to " +
            "RETURN w.from AS from, w.to AS to, SUM(w.linesOwned) AS linesOwned ORDER BY w.from")
    WeeklyOwnershipStats[] getWeeklyOwnershipStats(@Param("repoId") Long repoId, @Param("userId") Long userId, @Param("from") Instant from, @Param("to") Instant to);

    // Note - Spring Data REST handles `Iterable` result from query incorrectly - assumes it is CollectionModel
    @Query("MATCH (a:Alias)<-[:OF]-(w:WeeklyCommitStats)-[:OF]->(r:GitRepo) WHERE w.from >= $from AND w.to <= $to " +
            "RETURN w.from AS from, w.to AS to, SUM(w.commitCount) AS totalCommits, sum(w.linesAdded) AS linesAdded, sum(w.linesRemoved) AS linesRemoved ORDER BY w.from")
    WeeklyWorkStats[] getTotalWeeklyWorkStats(@Param("repoId") Long repoId, @Param("from") Instant from, @Param("to") Instant to);

    // Note - Spring Data REST handles `Iterable` result from query incorrectly - assumes it is CollectionModel
    @Query("MATCH (a:Alias)<-[:OF]-(w:TotalOwnershipStats)-[:OF]->(r:GitRepo) WHERE w.from >= $from AND w.to <= $to " +
            "RETURN w.from AS from, w.to AS to, SUM(w.linesOwned) AS linesOwned ORDER BY w.from")
    WeeklyOwnershipStats[] getTotalWeeklyOwnershipStats(@Param("repoId") Long repoId, @Param("from") Instant from, @Param("to") Instant to);

    @Query("MATCH (u:User)-[:ALIAS]->(a:Alias)-[:OF]->(r:GitRepo) WHERE id(r) = $repoId AND id(u) = $userId " +
            "WITH a,r,u MATCH (a)<-[:OF]-(w:WeeklyCommitStats)-[:OF]->(r:GitRepo) WHERE w.from >= $from AND w.to <= $to " +
            "WITH w,a,u MATCH (r:RemovedLines)-[:OF]->(w) " +
            "WITH w,r,a,u MATCH (a)<-[:FROM]-(ro:RemovedLines)-[:OF]->(wo)-[:OF]->(ao:Alias) WHERE w.from = wo.from AND NOT (u)-[:ALIAS]->(ao:Alias) " +
            "RETURN w.from AS from, w.to AS to, SUM(r.removedLines) AS removedLinesOfOthers, SUM(ro.removedLines) AS removedByOthersLines ORDER BY w.from")
    RemovedLinesWeeklyStats[] getRemovedLinesStats(@Param("repoId") Long repoId, @Param("userId") Long userId, @Param("from") Instant from, @Param("to") Instant to);

    @Query("MATCH (u:User)-[:ALIAS]->(a:Alias)-[:OF]->(r:GitRepo) WHERE id(r) = $repoId " +
            "WITH a,r,u MATCH (a)<-[:OF]-(w:WeeklyCommitStats)-[:OF]->(r:GitRepo) WHERE w.from >= $from AND w.to <= $to " +
            "WITH w,a,u MATCH (r:RemovedLines)-[:OF]->(w) " +
            "RETURN w.from AS from, w.to AS to, SUM(r.removedLines) AS removedLinesOfOthers, 0 AS removedByOthersLines ORDER BY w.from")
    RemovedLinesWeeklyStats[] getTotalRemovedLinesStats(@Param("repoId") Long repoId, @Param("from") Instant from, @Param("to") Instant to);
}
