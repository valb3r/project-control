package com.valb3r.projectcontrol.domain.stats;

import com.valb3r.projectcontrol.config.annotation.OnSaveValidationGroup;
import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.GitRepo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public abstract class CommitStats extends Accountable {

    @NotNull(groups = OnSaveValidationGroup.class)
    protected Instant from;

    @NotNull(groups = OnSaveValidationGroup.class)
    protected Instant to;

    @Min(value = 0, groups = OnSaveValidationGroup.class)
    protected long commitCount;

    @Min(value = 0, groups = OnSaveValidationGroup.class)
    protected long linesAdded;

    @Min(value = 0, groups = OnSaveValidationGroup.class)
    protected long linesRemoved;

    public CommitStats(GitRepo repo, Alias alias, @NotNull Instant from, @NotNull Instant to, @Min(0) long commitCount, @Min(0) long linesAdded, @Min(0) long linesRemoved) {
        super(repo, alias);
        this.from = from;
        this.to = to;
        this.commitCount = commitCount;
        this.linesAdded = linesAdded;
        this.linesRemoved = linesRemoved;
    }
}
