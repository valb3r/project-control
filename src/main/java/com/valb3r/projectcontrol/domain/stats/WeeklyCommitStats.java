package com.valb3r.projectcontrol.domain.stats;

import com.valb3r.projectcontrol.config.annotation.OnSaveValidationGroup;
import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.GitRepo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@NodeEntity
public class WeeklyCommitStats extends CommitStats {

    @Id
    @GeneratedValue
    private Long id;

    @Min(value = 0, groups = OnSaveValidationGroup.class)
    protected long removedLinesOfOtherAuthors;

    @Min(value = 0, groups = OnSaveValidationGroup.class)
    protected long removedOwnLines;

    @Min(value = 0, groups = OnSaveValidationGroup.class)
    protected long linesRemovedByOtherAuthors;

    @Builder
    public WeeklyCommitStats(GitRepo repo, Alias alias, @NotNull Instant from, @NotNull Instant to, @Min(0) long commitCount, @Min(0) long linesAdded, @Min(0) long linesRemoved, @Min(0) long linesOwned, Long id) {
        super(repo, alias, from, to, commitCount, linesAdded, linesRemoved, linesOwned);
        this.id = id;
    }
}
