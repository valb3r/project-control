package com.valb3r.projectcontrol.service.domainhandlers;

import com.valb3r.projectcontrol.config.annotation.AfterSaveDo;
import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.repository.GitRepoRepository;
import com.valb3r.projectcontrol.service.analyze.RepoAnalyzer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepoEventHandler {

    private final RepoAnalyzer analyzer;
    private final GitRepoRepository gitRepo;

    @AfterSaveDo
    @Transactional(propagation = Propagation.MANDATORY)
    public void afterSave(GitRepo repo) {
        if (repo.getAnalysisState() == GitRepo.AnalysisState.CLEANUP) {
            gitRepo.deleteAnalyzedDataById(repo.getId());
            repo.getWorkDoneBySteps().clear();
            repo.setAnalysisState(GitRepo.AnalysisState.CLEANED);
            repo.setCommitsProcessed(0L);
            gitRepo.save(repo);
            return;
        }

        if (repo.getAnalysisState() == GitRepo.AnalysisState.STARTED) {
            analyzer.analyze(repo);
        }
    }
}