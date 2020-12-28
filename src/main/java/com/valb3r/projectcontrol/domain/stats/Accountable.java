package com.valb3r.projectcontrol.domain.stats;

import com.valb3r.projectcontrol.domain.Project;
import com.valb3r.projectcontrol.domain.User;
import org.neo4j.ogm.annotation.Relationship;

public abstract class Accountable {

    public static final String OF_USER = "OF";
    public static final String OF_PROJECT = "OF";

    @Relationship(type = OF_USER)
    private User user;

    @Relationship(type = OF_PROJECT)
    private Project project;
}
