package com.valb3r.projectcontrol.domain;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public abstract class LinkableToProject {

    public static final String OF_PROJECT = "OF";

    @NotNull
    @Relationship(type = OF_PROJECT)
    private Project project;
}
