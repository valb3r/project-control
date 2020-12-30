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
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.neo4j.driver.internal.shaded.io.netty.util.internal.StringUtil;

import java.io.IOException;
import java.util.HashMap;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.eclipse.jgit.lib.Constants.HEAD;
import static org.kie.internal.io.ResourceFactory.newByteArrayResource;

@RequiredArgsConstructor
public abstract class CommitBasedAnalyzer {

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
        try (var walk = new RevWalk(git.getRepository())) {
            walk.setRevFilter(RevFilter.NO_MERGES);
            if (StringUtil.isNullOrEmpty(repo.getLastAnalyzedCommit())) {
                walk.sort(RevSort.COMMIT_TIME_DESC, true);
                walk.sort(RevSort.REVERSE, true);
            }

            walk.markStart(walk.parseCommit(git.getRepository().resolve(HEAD)));
            var container = container(repo);
            var counter = 0L;
            RevCommit commit;
            RevCommit prevCommit = null;
            while ((commit = walk.next()) != null) {
                if (commit.getId().getName().equals(repo.getLastAnalyzedCommit())) {
                    break;
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

                if (counter % SAVE_EACH_N_COMMIT == 0) {
                    repo.setCommitsProcessed(counter);
                    repo.setLastAnalyzedCommit(commit.getName());
                    stateUpdatingService.updateInternalData(repo);
                }
            }

            if (prevCommit != null) {
                repo.setCommitsProcessed(counter);
                repo.setLastAnalyzedCommit(prevCommit.getName());
                stateUpdatingService.updateInternalData(repo);
            }
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
}
