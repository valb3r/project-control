package com.valb3r.projectcontrol.domain.rules;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Data
@RequiredArgsConstructor
public class RuleContext {

    private final String path;
    private final String author;
    private final Instant commitDate;

    private boolean exclude;
    private boolean include;
}
