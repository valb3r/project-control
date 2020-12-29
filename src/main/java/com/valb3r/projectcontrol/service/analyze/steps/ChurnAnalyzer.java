package com.valb3r.projectcontrol.service.analyze.steps;

import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.repository.AliasRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Order(0)
@Service
@RequiredArgsConstructor
public class ChurnAnalyzer implements AnalysisStep {

    private final AliasRepository aliases;

    @Override
    @SneakyThrows
    public void execute(Git git, GitRepo repo) {
        var aliasCache = new HashMap<String, Alias>();
        var walk = new RevWalk(git.getRepository());
        RevCommit commit;
        while ((commit = walk.next()) != null) {
            var name = commit.getAuthorIdent().getName();
            var alias = aliasCache.computeIfAbsent(
                    name,
                    id -> aliases.findByNameAndRepoId(name, repo.getId())
                            .orElseGet(() -> aliases.save(Alias.builder().name(name).repo(repo).build()))
            );
        }
    }

    @Override
    public GitRepo.AnalysisState stateOnSuccess() {
        return GitRepo.AnalysisState.CHURN_COUNTED;
    }
}
