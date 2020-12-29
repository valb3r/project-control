package com.valb3r.projectcontrol.domain.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
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
