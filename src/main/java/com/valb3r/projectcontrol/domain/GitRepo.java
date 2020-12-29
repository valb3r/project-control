package com.valb3r.projectcontrol.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@ToString
@SuperBuilder
@NodeEntity
public class GitRepo {

    @Id
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String branchToAnalyze;

    @NotBlank
    private String url;

    @NotNull
    private UUID uuid;

    private boolean isPrivate;

    @NotNull
    @ReadOnlyProperty
    private AnalysisState analysisState;

    private Long commitsProcessed;

    private String errorMessage;

    public enum AnalysisState {
        NONE,
        STARTED,
        CLONED,
        CHURN_COUNTED,
        LOC_OWNERSHIP_COUNTED,
        FINISHED,
        FAILED
    }
}
