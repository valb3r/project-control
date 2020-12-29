package com.valb3r.projectcontrol.service.analyze.steps;

import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.domain.stats.WeeklyCommitStats;
import com.valb3r.projectcontrol.repository.AliasRepository;
import com.valb3r.projectcontrol.repository.FileExclusionRuleRepository;
import com.valb3r.projectcontrol.repository.FileInclusionRuleRepository;
import com.valb3r.projectcontrol.repository.WeeklyCommitStatsRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.kie.internal.io.ResourceFactory.newByteArrayResource;

@Order(0)
@Service
@RequiredArgsConstructor
public class ChurnAnalyzer implements AnalysisStep {

    private final AliasRepository aliases;
    private final WeeklyCommitStatsRepository commitStatsRepo;
    private final FileInclusionRuleRepository inclusionRepo;
    private final FileExclusionRuleRepository exclusionRepo;

    @Override
    @SneakyThrows
    public void execute(Git git, GitRepo repo) {
        var aliasCache = new HashMap<String, Alias>();
        var walk = new RevWalk(git.getRepository());
        var container = container(repo);
        RevCommit commit;
        while ((commit = walk.next()) != null) {
            var name = commit.getAuthorIdent().getName();
            var alias = aliasCache.computeIfAbsent(
                    name,
                    id -> aliases.findByNameAndRepoId(name, repo.getId())
                            .orElseGet(() -> aliases.save(Alias.builder().name(name).repo(repo).build()))
            );

            var analyzed = analyzeCommit(
                    AnalysisCtx.builder()
                            .container(container)
                            .repo(git.getRepository())
                            .commit(commit)
                            .walk(walk)
                            .build()
            );

            var stat = commitStatsRepo.findBy(alias.getId(), analyzed.getDate()).orElseGet(() -> WeeklyCommitStats.builder().alias(alias).repo(repo).build());
            stat.setLinesRemoved(stat.getLinesRemoved() + analyzed.getLinesDeleted());
            stat.setLinesAdded(stat.getLinesAdded() + analyzed.getLinesAdded());
        }
    }

    @Override
    public GitRepo.AnalysisState stateOnSuccess() {
        return GitRepo.AnalysisState.CHURN_COUNTED;
    }

    private KieContainer container(GitRepo repo) {
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

    @SneakyThrows
    private CommitStat analyzeCommit(AnalysisCtx ctx) {
        RevCommit parent = ctx.getRepo().parseCommit(ctx.getCommit().getParent(0).getId());
        try (DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
            df.setRepository(ctx.getRepo());
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);

            var diffs = ctx.getCommit().getParentCount() > 0
                    ? df.scan(parent.getTree(), ctx.getCommit().getTree())
                    : df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, ctx.getWalk().getObjectReader(), ctx.getCommit().getTree()));

            var stateless = ctx.getContainer().newStatelessKieSession();

            long linesDeleted = 0;
            long linesAdded = 0;

            for (var diff : diffs) {
                var context = new ChurnContext(diff.getNewPath());
                stateless.execute(context);

                if (context.isExclude() || !context.isInclude()) {
                    continue;
                }

                for (Edit edit : df.toFileHeader(diff).toEditList()) {
                    linesDeleted += edit.getEndA() - edit.getBeginA();
                    linesAdded += edit.getEndB() - edit.getBeginB();
                }
            }

            return new CommitStat(linesAdded, linesDeleted, ctx.getCommit().getAuthorIdent().getWhen().toInstant());
        }
    }

    @Data
    @RequiredArgsConstructor
    public static class ChurnContext {
        private final String path;

        private boolean exclude;
        private boolean include = true;
    }

    @Data
    @RequiredArgsConstructor
    private static class CommitStat {

        private final long linesAdded;
        private final long linesDeleted;
        private final Instant date;
    }
}
