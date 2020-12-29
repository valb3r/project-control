package com.valb3r.projectcontrol.domain.stats;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@Getter
@Setter
@ToString
@SuperBuilder
@NodeEntity
public class TotalCommitStats extends CommitStats {

    @Id
    private Long id;
}
