package com.valb3r.projectcontrol.service.analyze.steps;

import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.service.analyze.StateUpdatingService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.kie.api.runtime.KieContainer;
import org.neo4j.driver.internal.shaded.io.netty.util.internal.StringUtil;

import java.io.IOException;
import java.util.HashMap;

import static org.eclipse.jgit.lib.Constants.HEAD;

@RequiredArgsConstructor
public abstract class CommitBasedAnalyzer {

    private static final int SAVE_EACH_N_COMMIT = 10;

    private final StateUpdatingService stateUpdatingService;

    @SneakyThrows
    public Git execute(Git git, GitRepo repo) {
        analyzeRepo(git, repo);
        return git;
    }

    public void analyzeRepo(Git git, GitRepo repo) throws IOException {
        var aliasCache = new HashMap<String, Alias>();
        try (var walk = new RevWalk(git.getRepository())) {
            walk.setRevFilter(RevFilter.NO_MERGES);
            if (StringUtil.isNullOrEmpty(repo.getLastAnalyzedCommit())) {
                walk.sort(RevSort.COMMIT_TIME_DESC, true);
                walk.sort(RevSort.REVERSE, true);
            }

            walk.markStart(walk.parseCommit(git.getRepository().resolve(HEAD)));
            var container = container(repo);
            var counter = 0L;
            RevCommit commit;
            RevCommit lastCommit = null;
            while ((commit = walk.next()) != null) {
                if (commit.getId().getName().equals(repo.getLastAnalyzedCommit())) {
                    break;
                }

                processCommit(git, repo, aliasCache, walk, container, commit);
                counter++;

                lastCommit = commit;

                if (counter % SAVE_EACH_N_COMMIT == 0) {
                    repo.setCommitsProcessed(counter);
                    repo.setLastAnalyzedCommit(commit.getName());
                    stateUpdatingService.updateInternalData(repo);
                }
            }

            if (lastCommit != null) {
                repo.setCommitsProcessed(counter);
                repo.setLastAnalyzedCommit(lastCommit.getName());
                stateUpdatingService.updateInternalData(repo);
            }
        }
    }

    protected KieContainer container(GitRepo repo) {
        return null;
    }

    protected abstract void processCommit(Git git, GitRepo repo, HashMap<String, Alias> aliasCache, RevWalk walk, KieContainer container, RevCommit commit);
}
