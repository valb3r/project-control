package com.valb3r.projectcontrol.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public abstract class LinkableToRepo {

    public static final String OF_REPO = "OF";

    @NotNull
    @Relationship(type = OF_REPO)
    protected GitRepo repo;

    public LinkableToRepo(GitRepo repo) {
        this.repo = repo;
    }
}
