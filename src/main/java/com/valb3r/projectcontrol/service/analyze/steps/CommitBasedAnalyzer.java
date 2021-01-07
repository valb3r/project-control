package com.valb3r.projectcontrol.service.analyze.steps;

import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.repository.AliasRepository;
import com.valb3r.projectcontrol.repository.FileExclusionRuleRepository;
import com.valb3r.projectcontrol.repository.FileInclusionRuleRepository;
import com.valb3r.projectcontrol.service.analyze.StateUpdatingService;
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
            startCommit = walk.parseCommit(git.getRepository().resolve(HEAD));
            walk.markStart(startCommit);

            var container = container(repo);

            boolean inExcludeRange = false;
            while ((commit = walk.next()) != null) {
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
                        .container(container)
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

    protected KieContainer container(GitRepo repo) {
        var inclusion = inclusionRepo.findByRepoId(repo.getId());
        var exclusion = exclusionRepo.findByRepoId(repo.getId());
        KieServices services = KieServices.Factory.get();
        KieFileSystem fileSystem = services.newKieFileSystem();

        inclusion.forEach(it -> newByteArrayResource(it.getRule().getBytes(UTF_8)));
        exclusion.forEach(it -> newByteArrayResource(it.getRule().getBytes(UTF_8)));

        KieBuilder kb = services.newKieBuilder(fileSystem);
        kb.buildAll();
        KieModule kieModule = kb.getKieModule();

        return services.newKieContainer(kieModule.getReleaseId());
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
}
