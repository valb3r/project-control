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

import static org.eclipse.jgit.lib.Constants.HEAD;

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
            var initialState = repo.getLastGoodState();
            if (initialState == GitRepo.AnalysisState.NONE || initialState == GitRepo.AnalysisState.FINISHED) {
                repo.setLastOkAnalysedCommit(null);
                repo.setStartFromCommit(HEAD);
            }

            boolean hasCloneErrors = initialState == GitRepo.AnalysisState.CLONING || initialState == GitRepo.AnalysisState.CLONED;
            boolean hasAnalysisErrors = initialState != GitRepo.AnalysisState.NONE && initialState != GitRepo.AnalysisState.FINISHED && !hasCloneErrors;
            for (var step : steps) {
                if (null == git) {
                    stateUpdatingService.updateStatus(repo, step.stateOnStart());
                    git = step.execute(git, repo);
                    stateUpdatingService.updateStatus(repo, step.stateOnSuccess());
                    continue;
                }

                if (hasAnalysisErrors) {
                    if (initialState != step.stateOnStart() && initialState != step.stateOnSuccess()) {
                       continue;
                    }

                    if (initialState == step.stateOnSuccess()) {
                        hasAnalysisErrors = false;
                        repo.setLastOkAnalysedCommit(null);
                        continue;
                    }
                    hasAnalysisErrors = false;
                }

                stateUpdatingService.updateStatus(repo, step.stateOnStart());
                git = step.execute(git, repo);
                repo.setLastOkAnalysedCommit(null);
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
