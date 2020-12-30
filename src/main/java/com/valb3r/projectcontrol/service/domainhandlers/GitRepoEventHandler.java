package com.valb3r.projectcontrol.service.domainhandlers;

import com.valb3r.projectcontrol.config.annotation.BeforeSaveDo;
import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.repository.GitRepoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitRepoEventHandler {

    private final GitRepoRepository gitRepoRepository;

    @BeforeSaveDo
    @Transactional(propagation = Propagation.MANDATORY)
    public void afterSave(GitRepo repo) {
        if (null != repo.getBranchToAnalyze() && null != repo.getId()) {
            var dbRepo = gitRepoRepository.findById(repo.getId()).get();
            if (!dbRepo.getBranchToAnalyze().equals(repo.getBranchToAnalyze())) {
                throw new IllegalStateException("Unable to change branch name - it is immutable!");
            }
        }
    }
}