package com.valb3r.projectcontrol.service.analyze.steps;

import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.repository.AliasRepository;
import com.valb3r.projectcontrol.repository.FileExclusionRuleRepository;
import com.valb3r.projectcontrol.repository.FileInclusionRuleRepository;
import com.valb3r.projectcontrol.service.analyze.StateUpdatingService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.eclipse.jgit.lib.Constants.HEAD;
import static org.kie.internal.io.ResourceFactory.newByteArrayResource;

@Slf4j
@RequiredArgsConstructor
public abstract class CommitBasedAnalyzer implements AnalysisStep {

    public static final String RESOURCES_DIR = "src/main/resources/";

    private final AliasRepository aliases;
    private final StateUpdatingService stateUpdatingService;
    private final FileInclusionRuleRepository inclusionRepo;
    private final FileExclusionRuleRepository exclusionRepo;

    @SneakyThrows
    public Git execute(Git git, GitRepo repo) {
        analyzeRepo(git, repo);
        return git;
    }

    // FIXME Support of multiple analyzed ranges is necessary. It is possible to get (HEAD)1-new,2-analyzed,3-analyzed,4-new,5-analyzed,6-new(FIRST) sequence during multiple restarts as we traverse from HEAD
    protected void analyzeRepo(Git git, GitRepo repo) throws IOException {
        var aliasCache = new HashMap<String, Alias>();
        var counter = 0L;
        RevCommit commit;
        RevCommit prevCommit = null;

        var startCommit = repo.beginEndOfStep(stateOnStart());
        try (var walk = new RevWalk(git.getRepository())) {
            walk.setRevFilter(RevFilter.NO_MERGES);
            walk.markStart(walk.parseCommit(git.getRepository().resolve(HEAD)));
            List<RevCommit> commits = new ArrayList<>();
            while ((commit = walk.next()) != null) {
                commits.add(commit);
            }

            var kie = container(repo);

            boolean started = false;
            for (int i = commits.size() - 1; i >= 0; i--) {
                commit = commits.get(i);

                if (null != startCommit && !started) {
                    if (startCommit.equals(commit.getName())) {
                        started = true;
                    }
                    prevCommit = commit;
                    continue;
                }

                var ctx = CommitCtx.builder()
                        .git(git)
                        .repo(repo)
                        .aliasCache(aliasCache)
                        .walk(walk)
                        .container(kie.getContainer())
                        .hasInclusionRules(kie.isHasInclusionRules())
                        .hasExclusionRules(kie.isHasExclusionRules())
                        .commit(commit)
                        .prevCommit(prevCommit)
                        .build();

                log.info("[{}->{}] Analyzing commit {}", stateOnStart(), stateOnSuccess(), commit.getId());
                processCommit(ctx);
                counter++;

                prevCommit = commit;
                updateRepoLastCommit(repo, counter, commit);
            }

        } catch (Exception ex) {
            updateRepoLastCommit(repo, counter, prevCommit);
            throw ex;
        }
    }

    protected KieSetup container(GitRepo repo) {
        var inclusion = inclusionRepo.findByRepoId(repo.getId());
        var exclusion = exclusionRepo.findByRepoId(repo.getId());
        KieServices services = KieServices.Factory.get();
        KieFileSystem fileSystem = services.newKieFileSystem();

        inclusion.forEach(it -> fileSystem.write(RESOURCES_DIR + it.getName() + ".drl", newByteArrayResource(it.getRule().getBytes(UTF_8))));
        exclusion.forEach(it -> fileSystem.write(RESOURCES_DIR + it.getName() + ".drl", newByteArrayResource(it.getRule().getBytes(UTF_8))));

        KieBuilder kb = services.newKieBuilder(fileSystem);
        kb.buildAll();
        KieModule kieModule = kb.getKieModule();

        return new KieSetup(
                services.newKieContainer(kieModule.getReleaseId()),
                !inclusion.isEmpty(),
                !exclusion.isEmpty()
        );
    }

    protected Alias alias(String name, CommitCtx ctx) {
        return ctx.getAliasCache().computeIfAbsent(
                name,
                id -> aliases.findByNameAndRepoId(name, ctx.getRepo().getId())
                        .orElseGet(() -> aliases.save(Alias.builder().name(name).repo(ctx.getRepo()).build()))
        );
    }

    protected abstract void processCommit(CommitCtx ctx);

    private void updateRepoLastCommit(GitRepo repo, long count, RevCommit commit) {
        if (null == commit) {
            return;
        }

        repo.setCommitsProcessed((null != repo.getCommitsProcessed() ? repo.getCommitsProcessed() : 0L) + count);
        repo.reportAnalyzedRange(stateOnStart(), commit.getId().getName());
        stateUpdatingService.updateInternalData(repo);
    }

    @Getter
    @RequiredArgsConstructor
    protected static class KieSetup {

        private final KieContainer container;
        private final boolean hasInclusionRules;
        private final boolean hasExclusionRules;
    }
}
