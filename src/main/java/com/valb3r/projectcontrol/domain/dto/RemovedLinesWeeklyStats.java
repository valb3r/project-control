package com.valb3r.projectcontrol.domain.dto;

import lombok.Data;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.Instant;

@Data
@QueryResult
public class RemovedLinesWeeklyStats {

    private Instant from;
    private Instant to;
    private Long removedLinesOfOthers;
    private Long removedByOthersLines;
    private Long removedOwnLines;
}
