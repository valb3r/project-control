package com.valb3r.projectcontrol.service.analyze.steps;

import com.valb3r.projectcontrol.config.GitConfig;
import com.valb3r.projectcontrol.domain.GitRepo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Collections;

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
        return clone.call();
    }

    private void setCredentialsIfPossible(GitRepo repo, @SuppressWarnings("rawtypes") TransportCommand clone) {
        if (config.hasCredentials() && repo.isNeedsAuthentication()) {
            clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider(config.getUsername(), config.getPassword()));
        }
    }
}
