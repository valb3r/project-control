package com.valb3r.projectcontrol.domain.stats;

import com.valb3r.projectcontrol.config.annotation.OnSaveValidationGroup;
import com.valb3r.projectcontrol.domain.LinkableToRepo;
import com.valb3r.projectcontrol.domain.User;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;

public abstract class Accountable extends LinkableToRepo {

    public static final String OF_USER = "OF";

    @NotNull(groups = OnSaveValidationGroup.class)
    @Relationship(type = OF_USER)
    private User user;
}
