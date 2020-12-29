package com.valb3r.projectcontrol.domain;

import com.valb3r.projectcontrol.config.annotation.OnSaveValidationGroup;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@NodeEntity
public class FileExclusionRule extends LinkableToRepo {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(groups = OnSaveValidationGroup.class)
    private String name;

    @NotBlank(groups = OnSaveValidationGroup.class)
    private String rule;
}
