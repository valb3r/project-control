package com.valb3r.projectcontrol.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity
public class GitRepo {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String branchToAnalyze;

    @NotBlank
    private String url;

    @NotNull
    @Builder.Default
    private String uuid = UUID.randomUUID().toString();

    private boolean needsAuthentication;

    private String lastAnalyzedCommit;

    @NotNull
    @Builder.Default
    private AnalysisState analysisState = AnalysisState.NONE;

    @NotNull
    @Builder.Default
    private AnalysisState lastGoodState = AnalysisState.NONE;

    private Long commitsProcessed;

    private String errorMessage;

    public enum AnalysisState {
        NONE,
        STARTED,
        CLONING,
        CLONED,
        CHURN_COUNTING,
        CHURN_COUNTED,
        LOC_OWNERSHIP_COUNTED,
        FINISHED,
        FAILED;
    }
}
