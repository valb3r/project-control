package com.valb3r.projectcontrol.service.analyze.steps;

import com.valb3r.projectcontrol.config.GitConfig;
import com.valb3r.projectcontrol.domain.GitRepo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Collections;

@Slf4j
@Order(0)
@Service
@RequiredArgsConstructor
public class CloneRepoStep implements AnalysisStep {

    private final GitConfig config;

    @Override
    public Git execute(Git git, GitRepo repo) {
        return doCloneOrPull(repo);
    }

    @Override
    public GitRepo.AnalysisState stateOnStart() {
        return GitRepo.AnalysisState.CLONING;
    }

    @Override
    public GitRepo.AnalysisState stateOnSuccess() {
        return GitRepo.AnalysisState.CLONED;
    }

    @SneakyThrows
    private Git doCloneOrPull(GitRepo repo) {
        log.info("Updating local repo from remote {}", repo.getName());
        var repoPath = Paths.get(config.getReposPath()).resolve(repo.getUuid()).toFile();
        String branchName = "refs/heads/" + repo.getBranchToAnalyze();

        if (repoPath.exists()) {
            var git = Git.open(repoPath);
            var pull = git.pull().setRemoteBranchName(branchName);
            setCredentialsIfPossible(repo, pull);
            pull.call();
            logHeadRevision(git);
            return git;
        }

        var clone = Git.cloneRepository()
                .setURI(repo.getUrl())
                .setDirectory(Paths.get(config.getReposPath()).resolve(repo.getUuid()).toFile())
                .setBranchesToClone(Collections.singletonList(branchName));

        setCredentialsIfPossible(repo, clone);
        var git = clone.call();
        logHeadRevision(git);
        return git;
    }

    private void setCredentialsIfPossible(GitRepo repo, @SuppressWarnings("rawtypes") TransportCommand clone) {
        if (config.hasCredentials() && repo.isNeedsAuthentication()) {
            clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider(config.getUsername(), config.getPassword()));
        }
    }

    @SneakyThrows
    private void logHeadRevision(Git git) {
        try (var repo = git.getRepository()) {
            log.info("Pulled repo from remote, rev is {}", repo.resolve("HEAD"));
        }
    }
}
