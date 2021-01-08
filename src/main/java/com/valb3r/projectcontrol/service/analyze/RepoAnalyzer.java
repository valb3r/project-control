package com.valb3r.projectcontrol.service.analyze;

import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.service.analyze.steps.AnalysisStep;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepoAnalyzer {

    private final StateUpdatingService stateUpdatingService;
    private final List<AnalysisStep> steps;

    @Async
    @SneakyThrows
    public void analyze(GitRepo repo) {
        repo.setErrorMessage(null);
        Git git = null;
        try {
            for (var step : steps) {
                stateUpdatingService.updateStatus(repo, step.stateOnStart());
                git = step.execute(git, repo);
                stateUpdatingService.updateStatus(repo, step.stateOnSuccess());
            }
            stateUpdatingService.updateStatus(repo, GitRepo.AnalysisState.FINISHED);
        } catch (Exception ex) {
            log.error("Failed processing {}", repo.getName(), ex);
            repo.setErrorMessage(ex.getMessage() + " : " + ex.getCause() + "\n" + Arrays.toString(ex.getStackTrace()));
            stateUpdatingService.updateStatus(repo, GitRepo.AnalysisState.FAILED);
        }
    }
}
