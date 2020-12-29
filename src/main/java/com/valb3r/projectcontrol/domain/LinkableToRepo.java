package com.valb3r.projectcontrol.domain;

import com.valb3r.projectcontrol.config.annotation.OnSaveValidationGroup;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@SuperBuilder
public abstract class LinkableToRepo {

    public static final String OF_REPO = "OF";

    @NotNull(groups = OnSaveValidationGroup.class)
    @Relationship(type = OF_REPO)
    private GitRepo repo;
}
