package com.valb3r.projectcontrol.domain.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.Instant;

@Data
@QueryResult
public class WorkStats {

    private Instant from;
    private Instant to;
    private Long totalCommits;
    private Long linesAdded;
    private Long linesRemoved;
}
