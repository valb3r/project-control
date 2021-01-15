package com.valb3r.projectcontrol.domain.stats;

import com.valb3r.projectcontrol.config.annotation.OnSaveValidationGroup;
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
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity
public class TotalOwnershipStats extends Accountable {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull(groups = OnSaveValidationGroup.class)
    private Instant from;

    @NotNull(groups = OnSaveValidationGroup.class)
    private Instant to;

    @NotNull(groups = OnSaveValidationGroup.class)
    private Instant trueCommitTime;

    @Min(value = 0, groups = OnSaveValidationGroup.class)
    protected long linesOwned;

    @Builder
    public TotalOwnershipStats(GitRepo repo, Alias alias, Long id, @NotNull(groups = OnSaveValidationGroup.class) Instant from, @NotNull(groups = OnSaveValidationGroup.class) Instant to, @Min(value = 0, groups = OnSaveValidationGroup.class) long linesOwned, Instant trueCommitTime) {
        super(repo, alias);
        this.id = id;
        this.from = from;
        this.to = to;
        this.trueCommitTime = trueCommitTime;
        this.linesOwned = linesOwned;
    }
}
