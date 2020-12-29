package com.valb3r.projectcontrol.domain.stats;

import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.GitRepo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity
public class TotalCommitStats extends CommitStats {

    @Id
    @GeneratedValue
    private Long id;

    @Builder
    public TotalCommitStats(GitRepo repo, Alias alias, @NotNull Instant from, @NotNull Instant to, @Min(0) long commitCount, @Min(0) long linesAdded, @Min(0) long linesRemoved, @Min(0) long linesOwned, Long id) {
        super(repo, alias, from, to, commitCount, linesAdded, linesRemoved, linesOwned);
        this.id = id;
    }
}
