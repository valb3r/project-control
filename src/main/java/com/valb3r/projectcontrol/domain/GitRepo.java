package com.valb3r.projectcontrol.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
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

    @JsonProperty("lastAnalyzedCommit")
    public String getLastAnalyzedCommit() {
        if (!workDoneBySteps.isEmpty()) {
            return workDoneBySteps.get(workDoneBySteps.size() - 1).split(":")[2];
        }

        return null;
    }

    public AnalyzedRange beginEndOfStep(AnalysisState start) {
        Map<String, AnalyzedRange> ranges = ranges();
        return ranges.getOrDefault(start.name(), new AnalyzedRange(null, null));
    }

    public void reportAnalyzedRange(AnalysisState start, AnalyzedRange range) {
        Map<String, AnalyzedRange> ranges = ranges();
        ranges.put(start.name(), range);
        setWorkDoneBySteps(
                ranges.entrySet().stream()
                        .map(it -> String.format("%s:%s:%s", it.getKey(), it.getValue().getStart(), it.getValue().getEnd()))
                        .collect(Collectors.toList())
        );
    }

    private Map<String, AnalyzedRange> ranges() {
        Map<String, AnalyzedRange> ranges = new LinkedHashMap<>();
        workDoneBySteps.forEach(it -> {
            var split = it.split(":");
            ranges.put(split[0], new AnalyzedRange(split[1], split[2]));
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

    @Getter
    @RequiredArgsConstructor
    public static class AnalyzedRange {
        private final String start;
        private final String end;
    }
}
