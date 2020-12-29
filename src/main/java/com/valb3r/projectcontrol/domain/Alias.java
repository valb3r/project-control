package com.valb3r.projectcontrol.domain;

import com.valb3r.projectcontrol.config.annotation.OnSaveValidationGroup;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor
@NodeEntity
public class Alias extends LinkableToRepo {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(groups = OnSaveValidationGroup.class)
    private String name;

    private User user;

    @Builder
    public Alias(GitRepo repo, Long id, @NotBlank String name, User user) {
        super(repo);
        this.id = id;
        this.name = name;
        this.user = user;
    }
}
