package com.valb3r.projectcontrol.service.analyze;

import com.valb3r.projectcontrol.config.GitConfig;
import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.service.analyze.steps.AnalysisStep;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepoAnalyzer {

    private final GitConfig config;
    private final StateUpdatingService stateUpdatingService;
    private final List<AnalysisStep> steps;

    @SneakyThrows
    public void analyze(GitRepo repo) {
        try {
            var git = doClone(repo);
            steps.forEach(it -> {
                it.execute(git, repo);
                stateUpdatingService.updateStatus(repo, it.stateOnSuccess());
            });
        } catch (Exception ex) {
            repo.setErrorMessage(ex.getMessage() + " : " + ex.getCause());
            stateUpdatingService.updateStatus(repo, GitRepo.AnalysisState.FAILED);
        }

    }

    @SneakyThrows
    private Git doClone(GitRepo repo) {
        var clone = Git.cloneRepository()
                .setURI(repo.getUrl())
                .setDirectory(Paths.get(config.getReposPath()).resolve(repo.getUuid().toString()).toFile())
                .setBranchesToClone(Collections.singletonList("refs/heads/" + repo.getBranchToAnalyze()))
                .setNoCheckout(true);

        if (config.hasCredentials() && repo.isPrivate()) {
            clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider(config.getUsername(), config.getPassword()));
        }
        var git = clone.call();

        stateUpdatingService.updateStatus(repo, GitRepo.AnalysisState.CLONED);
        return git;
    }
}
