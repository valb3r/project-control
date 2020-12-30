package com.valb3r.projectcontrol.service.analyze.steps;

import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.repository.GitRepoRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.kie.api.runtime.KieContainer;

import java.io.IOException;
import java.util.HashMap;

import static org.eclipse.jgit.lib.Constants.HEAD;

@RequiredArgsConstructor
public abstract class CommitBasedAnalyzer {

    private static final int SAVE_EACH_N_COMMIT = 10;

    private final GitRepoRepository gitRepoRepository;

    @SneakyThrows
    public Git execute(Git git, GitRepo repo) {
        analyzeRepo(git, repo);
        return git;
    }

    public void analyzeRepo(Git git, GitRepo repo) throws IOException {
        var aliasCache = new HashMap<String, Alias>();
        try (var walk = new RevWalk(git.getRepository())) {
            walk.markStart(walk.parseCommit(git.getRepository().resolve(HEAD)));
            var container = container(repo);
            var counter = 0L;
            RevCommit commit;
            while ((commit = walk.next()) != null) {
                if (commit.getId().getName().equals(repo.getLastAnalyzedCommit())) {
                    break;
                }

                processCommit(git, repo, aliasCache, walk, container, commit);
                counter++;

                if (counter % SAVE_EACH_N_COMMIT == 0) {
                    repo.setCommitsProcessed(counter);
                    repo.setLastAnalyzedCommit(commit.getName());
                    gitRepoRepository.save(repo);
                }
            }
            repo.setCommitsProcessed(counter);
        }
    }

    protected KieContainer container(GitRepo repo) {
        return null;
    }

    protected abstract void processCommit(Git git, GitRepo repo, HashMap<String, Alias> aliasCache, RevWalk walk, KieContainer container, RevCommit commit);
}
