package com.valb3r.projectcontrol.domain.stats;

import com.valb3r.projectcontrol.config.annotation.OnSaveValidationGroup;
import com.valb3r.projectcontrol.domain.Alias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity
public class RemovedLines {

    public static final String OF_WEEKLY = "OF";
    public static final String FROM_AUTHOR = "FROM";

    @Id
    @GeneratedValue
    private Long id;

    @Min(value = 0, groups = OnSaveValidationGroup.class)
    private long removedLines;

    @NotNull
    @Relationship(type = FROM_AUTHOR)
    private Alias fromAuthor;

    @NotNull
    @Relationship(type = OF_WEEKLY)
    private WeeklyCommitStats weekly;
}
