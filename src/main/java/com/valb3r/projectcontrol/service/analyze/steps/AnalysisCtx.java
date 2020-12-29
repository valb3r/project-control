package com.valb3r.projectcontrol.service.analyze.steps;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.kie.api.runtime.KieContainer;

@Getter
@Builder
@RequiredArgsConstructor
public class AnalysisCtx {

    private final RevCommit commit;
    private final RevWalk walk;
    private final Repository repo;
    private final KieContainer container;
}
