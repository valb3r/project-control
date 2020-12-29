package com.valb3r.projectcontrol.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@NodeEntity
public class GitRepo {

    @Id
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String url;

    @NotNull
    @ReadOnlyProperty
    private AnalysisState analysisState;

    public enum AnalysisState {
        NONE,
        STARTED,
        CLONED,
        CHURN_COUNTED,
        LOC_OWNERSHIP_COUNTED,
        FINISHED
    }
}
