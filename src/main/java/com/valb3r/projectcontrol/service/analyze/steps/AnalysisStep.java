package com.valb3r.projectcontrol.service.analyze.steps;

import com.valb3r.projectcontrol.domain.GitRepo;
import org.eclipse.jgit.api.Git;

public interface AnalysisStep {

    void execute(Git git, GitRepo repo);
    GitRepo.AnalysisState stateOnSuccess();
}
