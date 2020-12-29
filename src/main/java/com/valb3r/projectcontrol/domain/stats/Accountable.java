package com.valb3r.projectcontrol.domain.stats;

import com.valb3r.projectcontrol.config.annotation.OnSaveValidationGroup;
import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.LinkableToRepo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@SuperBuilder
public abstract class Accountable extends LinkableToRepo {

    public static final String OF_ALIAS = "OF";

    @NotNull(groups = OnSaveValidationGroup.class)
    @Relationship(type = OF_ALIAS)
    private Alias alias;
}
