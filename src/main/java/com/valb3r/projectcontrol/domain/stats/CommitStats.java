package com.valb3r.projectcontrol.domain.stats;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
@ToString
@SuperBuilder
public abstract class CommitStats extends Accountable {

    @NotNull
    private Instant from;

    @NotNull
    private Instant to;

    @Min(0)
    private long linesAdded;

    @Min(0)
    private long churn;

    @Min(0)
    private long linesOwned;
}
