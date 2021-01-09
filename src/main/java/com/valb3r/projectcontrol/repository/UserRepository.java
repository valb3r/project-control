package com.valb3r.projectcontrol.repository;

import com.valb3r.projectcontrol.domain.User;
import com.valb3r.projectcontrol.domain.dto.WeeklyWorkStats;
import com.valb3r.projectcontrol.repository.domain.UserExcerpt;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.valb3r.projectcontrol.config.Neo4jFullTextIndexManagement.USER_IDX;

@Repository
@RepositoryRestResource(excerptProjection = UserExcerpt.class)
public interface UserRepository extends Neo4jRepository<User, Long> {

    @Query("CALL db.index.fulltext.queryNodes(\"" + USER_IDX + "\", $name) YIELD node, score RETURN node ORDER BY score DESC")
    List<User> findByFullText(@Param("name") String name);

    @Query("MATCH (u:User)-[a:ALIAS]->(o:Alias)-[:OF]->(r:GitRepo) WHERE id(r) = $repoId RETURN u,a,o")
    List<User> findByRepoId(@Param("repoId") Long repoId);

    @Query("MATCH (u:User)-[:ALIAS]->(a:Alias)-[:OF]->(r:GitRepo) WHERE id(r) = $repoId AND id(u) = $userId " +
        "WITH a,r MATCH (a)<-[:OF]-(w:WeeklyCommitStats)-[:OF]->(r:GitRepo) WHERE w.from = $from " +
        "RETURN sum(w.commitCount) AS totalCommits")
    Optional<WeeklyWorkStats> getWeeklyWorkStats(@Param("userId") Long userId, @Param("repoId") Long repoId, @Param("from") Instant start);
}
