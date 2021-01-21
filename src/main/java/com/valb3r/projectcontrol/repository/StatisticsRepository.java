package com.valb3r.projectcontrol.repository;

import com.valb3r.projectcontrol.domain.Statistics;
import com.valb3r.projectcontrol.domain.dto.DateRange;
import com.valb3r.projectcontrol.domain.dto.RemovedLinesWeeklyStats;
import com.valb3r.projectcontrol.domain.dto.OwnershipStats;
import com.valb3r.projectcontrol.domain.dto.WorkStats;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
@RepositoryRestResource
public interface StatisticsRepository extends org.springframework.data.repository.Repository<Statistics, Long> {

    // Note - Spring Data REST handles `Iterable` result from query incorrectly - assumes it is CollectionModel
    @Query("MATCH (u:User)-[:ALIAS]->(a:Alias)-[:OF]->(r:GitRepo) WHERE id(r) = $repoId AND id(u) = $userId " +
            "WITH a,r MATCH (a)<-[:OF]-(w:WeeklyCommitStats)-[:OF]->(r:GitRepo) WHERE w.from >= $from AND w.to <= $to " +
            "RETURN w.from AS from, w.to AS to, SUM(w.commitCount) AS totalCommits, sum(w.linesAdded) AS linesAdded, sum(w.linesRemoved) AS linesRemoved ORDER BY w.from")
    WorkStats[] getWeeklyWorkStats(@Param("repoId") Long repoId, @Param("userId") Long userId, @Param("from") Instant from, @Param("to") Instant to);

    // Note - Spring Data REST handles `Iterable` result from query incorrectly - assumes it is CollectionModel
    @Query("MATCH (u:User)-[:ALIAS]->(a:Alias)-[:OF]->(r:GitRepo) WHERE id(r) = $repoId AND id(u) = $userId " +
            "WITH a,r MATCH (a)<-[:OF]-(w:TotalOwnershipStats)-[:OF]->(r:GitRepo) WHERE w.from >= $from AND w.to <= $to " +
            "RETURN w.from AS from, w.to AS to, SUM(w.linesOwned) AS linesOwned ORDER BY w.from")
    OwnershipStats[] getWeeklyOwnershipStats(@Param("repoId") Long repoId, @Param("userId") Long userId, @Param("from") Instant from, @Param("to") Instant to);

    // Note - Spring Data REST handles `Iterable` result from query incorrectly - assumes it is CollectionModel
    @Query("MATCH (a:Alias)<-[:OF]-(w:WeeklyCommitStats)-[:OF]->(r:GitRepo) WHERE id(r) = $repoId AND w.from >= $from AND w.to <= $to " +
            "RETURN w.from AS from, w.to AS to, SUM(w.commitCount) AS totalCommits, sum(w.linesAdded) AS linesAdded, sum(w.linesRemoved) AS linesRemoved ORDER BY w.from")
    WorkStats[] getTotalWorkStats(@Param("repoId") Long repoId, @Param("from") Instant from, @Param("to") Instant to);

    // Note - Spring Data REST handles `Iterable` result from query incorrectly - assumes it is CollectionModel
    @Query("MATCH (a:Alias)<-[:OF]-(w:TotalOwnershipStats)-[:OF]->(r:GitRepo) WHERE id(r) = $repoId AND w.from >= $from AND w.to <= $to " +
            "RETURN w.from AS from, w.to AS to, SUM(w.linesOwned) AS linesOwned ORDER BY w.from")
    OwnershipStats[] getTotalOwnershipStats(@Param("repoId") Long repoId, @Param("from") Instant from, @Param("to") Instant to);

    @Query("MATCH (r:GitRepo),(u:User) WHERE id(r) = $repoId AND id(u) = $userId " +
            "WITH u,r MATCH (a)<-[:OF]-(w:WeeklyCommitStats)-[:OF]->(r:GitRepo) WHERE w.from >= $from AND w.to <= $to " +
            "WITH w,u MATCH (r:RemovedLines)-[:OF]->(w) " +
            "CALL {" +
            "  WITH u,r MATCH (af)<-[:FROM]-(r)-[c:OF]->(w:WeeklyCommitStats)-[:OF]->(ao:Alias) WHERE (u)-[:ALIAS]->(af:Alias) AND (u)-[:ALIAS]->(ao:Alias) RETURN w.from AS from, w.to AS to, SUM(r.removedLines) AS removedOwnLines, 0  AS removedByOthersLines, 0 AS removedLinesOfOthers" +
            "  UNION WITH u,r MATCH (af)<-[:FROM]-(r)-[c:OF]->(w:WeeklyCommitStats)-[:OF]->(ao:Alias) WHERE NOT (u)-[:ALIAS]->(af:Alias) AND (u)-[:ALIAS]->(ao:Alias) RETURN w.from AS from, w.to AS to, SUM(r.removedLines) AS removedByOthersLines, 0 AS removedOwnLines, 0 AS removedLinesOfOthers" +
            "  UNION WITH u,r OPTIONAL MATCH (af)<-[:FROM]-(r)-[c:OF]->(w:WeeklyCommitStats)-[:OF]->(ao:Alias) WHERE (u)-[:ALIAS]->(af:Alias) AND NOT (u)-[:ALIAS]->(ao:Alias) RETURN w.from AS from, w.to AS to, SUM(r.removedLines) AS removedLinesOfOthers, 0 AS removedByOthersLines, 0 AS removedOwnLines" +
            "} " +
            "RETURN w.from AS from, w.to AS to, SUM(removedLinesOfOthers) AS removedLinesOfOthers, SUM(removedByOthersLines) AS removedByOthersLines, SUM(removedOwnLines) AS removedOwnLines ORDER BY w.from")
    RemovedLinesWeeklyStats[] getRemovedLinesStats(@Param("repoId") Long repoId, @Param("userId") Long userId, @Param("from") Instant from, @Param("to") Instant to);

    @Query("MATCH (u:User)-[:ALIAS]->(a:Alias)-[:OF]->(r:GitRepo) WHERE id(r) = $repoId " +
            "WITH a,r,u MATCH (a)<-[:OF]-(w:WeeklyCommitStats)-[:OF]->(r:GitRepo) WHERE w.from >= $from AND w.to <= $to " +
            "WITH w,a,u MATCH (r:RemovedLines)-[:OF]->(w) " +
            "RETURN w.from AS from, w.to AS to, SUM(r.removedLines) AS removedLinesOfOthers, 0 AS removedByOthersLines ORDER BY w.from")
    RemovedLinesWeeklyStats[] getTotalRemovedLinesStats(@Param("repoId") Long repoId, @Param("from") Instant from, @Param("to") Instant to);

    @Query("MATCH (u:User)-[:ALIAS]->(a:Alias)-[:OF]->(r:GitRepo) WHERE id(r) = $repoId AND id(u) = $userId " +
            "WITH a,r MATCH (a)<-[:OF]-(w:WeeklyCommitStats)-[:OF]->(r:GitRepo) " +
            "RETURN MIN(w.from) AS from, MAX(w.to) AS to")
    DateRange getWorkDateRanges(@Param("repoId") Long repoId, @Param("userId") Long userId);

    @Query("MATCH (u:User)-[:ALIAS]->(a:Alias)-[:OF]->(r:GitRepo) WHERE id(r) = $repoId " +
            "WITH a,r MATCH (a)<-[:OF]-(w:WeeklyCommitStats)-[:OF]->(r:GitRepo) " +
            "RETURN MIN(w.from) AS from, MAX(w.to) AS to")
    DateRange getTotalWorkDateRanges(@Param("repoId") Long repoId);
}
