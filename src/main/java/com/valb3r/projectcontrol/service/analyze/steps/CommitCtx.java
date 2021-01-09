package com.valb3r.projectcontrol.service.analyze.steps;

import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.GitRepo;
import lombok.Builder;
import lombok.Getter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.kie.api.runtime.KieContainer;

import java.util.HashMap;

@Getter
@Builder
public class CommitCtx {

    private final Git git;
    private final GitRepo repo;
    private final HashMap<String, Alias> aliasCache;
    private final RevWalk walk;
    private final KieContainer container;
    private final boolean hasInclusionRules;
    private final boolean hasExclusionRules;
    private final RevCommit prevCommit;
    private final RevCommit commit;
}
