package com.valb3r.projectcontrol.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@SuperBuilder
@NodeEntity
public class Alias extends LinkableToRepo {

    @Id
    private Long id;

    @NotBlank
    private String name;

    private User user;
}
