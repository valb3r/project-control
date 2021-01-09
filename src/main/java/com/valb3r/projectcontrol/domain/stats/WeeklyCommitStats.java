package com.valb3r.projectcontrol.domain.stats;

import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.GitRepo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.valb3r.projectcontrol.domain.stats.RemovedLines.OF_WEEKLY;

@Getter
@Setter
@NoArgsConstructor
@NodeEntity
public class WeeklyCommitStats extends CommitStats {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Relationship(type = OF_WEEKLY)
    private List<RemovedLines> linesRemovedOf = new ArrayList<>();

    @Builder
    public WeeklyCommitStats(GitRepo repo, Alias alias, @NotNull Instant from, @NotNull Instant to, @Min(0) long commitCount, @Min(0) long linesAdded, @Min(0) long linesRemoved, Long id) {
        super(repo, alias, from, to, commitCount, linesAdded, linesRemoved);
        this.id = id;
    }
}
