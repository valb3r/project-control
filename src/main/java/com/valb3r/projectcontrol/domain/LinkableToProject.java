package com.valb3r.projectcontrol.domain;

import com.valb3r.projectcontrol.config.annotation.OnSaveValidationGroup;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;

public abstract class LinkableToProject {

    public static final String OF_PROJECT = "OF";

    @NotNull(groups = OnSaveValidationGroup.class)
    @Relationship(type = OF_PROJECT)
    private Project project;
}
