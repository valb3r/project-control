package com.valb3r.projectcontrol.domain.stats;

import com.valb3r.projectcontrol.domain.Project;
import com.valb3r.projectcontrol.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.xml.catalog.Catalog;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class CommitStats extends Accountable {

    private Instant from;
    private Instant to;

    private long linesAdded;
    private long churn;
    private long linesOwned;
}
