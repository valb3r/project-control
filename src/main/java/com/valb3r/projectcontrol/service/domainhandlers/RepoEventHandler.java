package com.valb3r.projectcontrol.service.domainhandlers;

import com.valb3r.projectcontrol.config.annotation.AfterSaveDo;
import com.valb3r.projectcontrol.domain.GitRepo;
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

    @AfterSaveDo
    @Transactional(propagation = Propagation.MANDATORY)
    public void afterSave(GitRepo repo) {
        if (repo.getAnalysisState() != GitRepo.AnalysisState.STARTED) {
            return;
        }

        analyzer.analyze(repo);
    }
}