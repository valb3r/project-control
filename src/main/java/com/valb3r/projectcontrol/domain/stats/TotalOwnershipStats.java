package com.valb3r.projectcontrol.domain.stats;

import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.GitRepo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity
public class TotalOwnershipStats extends Accountable {

    @Id
    @GeneratedValue
    private Long id;

    @Builder
    public TotalOwnershipStats(GitRepo repo, Alias alias, Long id) {
        super(repo, alias);
        this.id = id;
    }
}
