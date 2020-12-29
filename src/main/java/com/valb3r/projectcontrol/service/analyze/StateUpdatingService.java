package com.valb3r.projectcontrol.service.analyze;

import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.repository.GitRepoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StateUpdatingService {

    private final GitRepoRepository gitRepoRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(GitRepo repo, GitRepo.AnalysisState analysisState) {
        repo.setAnalysisState(analysisState);
        gitRepoRepository.save(repo);
    }
}
