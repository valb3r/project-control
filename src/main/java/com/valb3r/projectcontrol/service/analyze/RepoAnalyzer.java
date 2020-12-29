package com.valb3r.projectcontrol.service.analyze;

import com.valb3r.projectcontrol.config.GitConfig;
import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.service.analyze.steps.AnalysisStep;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepoAnalyzer {

    private final GitConfig config;
    private final StateUpdatingService stateUpdatingService;
    private final List<AnalysisStep> steps;

    @Async
    @SneakyThrows
    public void analyze(GitRepo repo) {
        repo.setErrorMessage(null);
        stateUpdatingService.updateStatus(repo, GitRepo.AnalysisState.CLONING);
        try {
            var git = doCloneOrPull(repo);
            steps.forEach(it -> {
                it.execute(git, repo);
                stateUpdatingService.updateStatus(repo, it.stateOnSuccess());
            });
        } catch (Exception ex) {
            log.error("Failed processing {}", repo.getName(), ex);
            repo.setErrorMessage(ex.getMessage() + " : " + ex.getCause() + "\n" + Arrays.toString(ex.getStackTrace()));
            stateUpdatingService.updateStatus(repo, GitRepo.AnalysisState.FAILED);
        }

    }

    @SneakyThrows
    private Git doCloneOrPull(GitRepo repo) {
        var repoPath = Paths.get(config.getReposPath()).resolve(repo.getUuid()).toFile();
        String branchName = "refs/heads/" + repo.getBranchToAnalyze();

        if (repoPath.exists()) {
            var git = Git.open(repoPath);
            var pull = git.pull().setRemoteBranchName(branchName);
            setCredentialsIfPossible(repo, pull);
            pull.call();

            return git;
        }

        var clone = Git.cloneRepository()
                .setURI(repo.getUrl())
                .setDirectory(Paths.get(config.getReposPath()).resolve(repo.getUuid()).toFile())
                .setBranchesToClone(Collections.singletonList(branchName))
                .setNoCheckout(true);

        setCredentialsIfPossible(repo, clone);
        var git = clone.call();

        stateUpdatingService.updateStatus(repo, GitRepo.AnalysisState.CLONED);
        return git;
    }

    private void setCredentialsIfPossible(GitRepo repo, @SuppressWarnings("rawtypes") TransportCommand clone) {
        if (config.hasCredentials() && repo.isNeedsAuthentication()) {
            clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider(config.getUsername(), config.getPassword()));
        }
    }
}
