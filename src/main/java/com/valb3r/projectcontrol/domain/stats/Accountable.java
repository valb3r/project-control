package com.valb3r.projectcontrol.domain.stats;

import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.domain.LinkableToRepo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public abstract class Accountable extends LinkableToRepo {

    public static final String OF_ALIAS = "OF";

    @NotNull
    @Relationship(type = OF_ALIAS)
    protected Alias alias;

    public Accountable(GitRepo repo, Alias alias) {
        super(repo);
        this.alias = alias;
    }
}
