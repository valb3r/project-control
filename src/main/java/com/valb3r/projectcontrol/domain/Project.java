package com.valb3r.projectcontrol.domain;

import com.valb3r.projectcontrol.config.annotation.OnSaveValidationGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@NodeEntity
public class Project {

    public static final String OF_REPOS = "OF";
    public static final String OF_USERS = "OF";

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(groups = OnSaveValidationGroup.class)
    private String name;

    @NotNull(groups = OnSaveValidationGroup.class)
    @Builder.Default
    private Status status = Status.CREATED;

    @NotEmpty
    @Relationship(OF_REPOS)
    private List<GitRepo> repos;

    @NotEmpty
    @Relationship(OF_USERS)
    private List<User> users;

    public enum Status {
        CREATED,
        ANALYZE,
        ANALYZED
    }
}
