package com.valb3r.projectcontrol.service.analyze;

import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.repository.GitRepoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

@Service
@RequiredArgsConstructor
public class StateUpdatingService {

    private final GitRepoRepository gitRepoRepository;
    private final TransactionOperations operations;

    public void updateStatus(GitRepo repo, GitRepo.AnalysisState analysisState) {
        operations.executeWithoutResult(transactionStatus -> {
            if (repo.getAnalysisState() != GitRepo.AnalysisState.FAILED) {
                repo.setLastGoodState(repo.getAnalysisState());
            }

            if (analysisState == GitRepo.AnalysisState.FINISHED) {
                repo.setLastGoodState(GitRepo.AnalysisState.FINISHED);
            }

            repo.setAnalysisState(analysisState);
            gitRepoRepository.save(repo);
        });
    }
}
