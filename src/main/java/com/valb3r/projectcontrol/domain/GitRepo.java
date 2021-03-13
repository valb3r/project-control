package com.valb3r.projectcontrol.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

    private String startFromCommit;

    @Builder.Default
    private List<String> workDoneBySteps = new ArrayList<>();

    @NotNull
    @Builder.Default
    private AnalysisState analysisState = AnalysisState.NONE;

    private Long commitsProcessed;

    private String errorMessage;

    @CreatedDate
    @ReadOnlyProperty
    private Instant createdAt;

    @LastModifiedDate
    @ReadOnlyProperty
    private Instant modifiedAt;

    @JsonProperty("lastAnalyzedCommit")
    public String getLastAnalyzedCommit() {
        if (!workDoneBySteps.isEmpty()) {
            return workDoneBySteps.get(workDoneBySteps.size() - 1).split(":")[1];
        }

        return null;
    }

    public String beginEndOfStep(AnalysisState start) {
        Map<String, String> ranges = ranges();
        return ranges.getOrDefault(start.name(), null);
    }

    public void reportAnalyzedRange(AnalysisState start, String commitId) {
        Map<String, String> ranges = ranges();
        ranges.put(start.name(), commitId);
        setWorkDoneBySteps(
                ranges.entrySet().stream()
                        .map(it -> String.format("%s:%s", it.getKey(), it.getValue()))
                        .collect(Collectors.toList())
        );
    }

    private Map<String, String> ranges() {
        Map<String, String> ranges = new LinkedHashMap<>();
        workDoneBySteps.forEach(it -> {
            var split = it.split(":");
            ranges.put(split[0], split[1]);
        });
        return ranges;
    }

    public enum AnalysisState {
        NONE,
        CLEANUP,
        CLEANED,
        STARTED,
        CLONING,
        CLONED,
        CHURN_COUNTING,
        CHURN_COUNTED,
        LOC_OWNERSHIP_COUNTING,
        LOC_OWNERSHIP_COUNTED,
        REFACTOR_COUNTING,
        REFACTOR_COUNTED,
        FINISHED,
        FAILED
    }
}
