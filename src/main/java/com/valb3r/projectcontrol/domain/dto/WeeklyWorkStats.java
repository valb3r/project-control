package com.valb3r.projectcontrol.domain.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.annotation.QueryResult;

@Data
@QueryResult
public class WeeklyWorkStats {

    private Long totalCommits;
}
