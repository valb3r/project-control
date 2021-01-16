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
import java.util.HashMap;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.eclipse.jgit.lib.Constants.HEAD;
import static org.kie.internal.io.ResourceFactory.newByteArrayResource;

@RequiredArgsConstructor
public abstract class CommitBasedAnalyzer implements AnalysisStep {

    private static final int SAVE_EACH_N_COMMIT = 10;
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

    protected void analyzeRepo(Git git, GitRepo repo) throws IOException {
        var aliasCache = new HashMap<String, Alias>();
        var counter = 0L;
        RevCommit commit;
        RevCommit prevCommit = null;
        RevCommit startCommit = null;

        var range = repo.beginEndOfStep(stateOnStart());
        try (var walk = new RevWalk(git.getRepository())) {
            walk.setRevFilter(RevFilter.NO_MERGES);
            walk.markStart(walk.parseCommit(git.getRepository().resolve(HEAD)));

            var kie = container(repo);

            boolean inExcludeRange = false;
            while ((commit = walk.next()) != null) {
                if (null == startCommit) {
                    startCommit = commit;
                }

                if (commit.getName().equals(range.getStart())) {
                    inExcludeRange = !Objects.equals(range.getStart(), range.getEnd());
                    continue;
                }
                if (commit.getName().equals(range.getEnd())) {
                    inExcludeRange = false;
                    continue;
                }
                if (inExcludeRange) {
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

                processCommit(ctx);
                counter++;

                prevCommit = commit;
                updateRepoLastCommit(repo, counter, startCommit, commit, counter % SAVE_EACH_N_COMMIT == 0);
            }

            updateRepoLastCommit(repo, counter, startCommit, prevCommit, prevCommit != null);
        } catch (Exception ex) {
            updateRepoLastCommit(repo, counter, startCommit, prevCommit, prevCommit != null);
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

    private void updateRepoLastCommit(GitRepo repo, long counter, RevCommit startCommit, RevCommit endCommit, boolean needUpdate) {
        if (needUpdate) {
            repo.setCommitsProcessed(counter);
            repo.reportAnalyzedRange(stateOnStart(), new GitRepo.AnalyzedRange(startCommit.getName(), endCommit.getName()));
            stateUpdatingService.updateInternalData(repo);
        }
    }

    @Getter
    @RequiredArgsConstructor
    protected static class KieSetup {

        private final KieContainer container;
        private final boolean hasInclusionRules;
        private final boolean hasExclusionRules;
    }
}
