package com.valb3r.projectcontrol.domain.dto;

import lombok.Data;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.Instant;

@Data
@QueryResult
public class DateRange {

    private Instant from;
    private Instant to;
}
