package com.valb3r.projectcontrol.service.analyze.steps;

import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.domain.stats.WeeklyCommitStats;
import com.valb3r.projectcontrol.repository.AliasRepository;
import com.valb3r.projectcontrol.repository.FileExclusionRuleRepository;
import com.valb3r.projectcontrol.repository.FileInclusionRuleRepository;
import com.valb3r.projectcontrol.repository.GitRepoRepository;
import com.valb3r.projectcontrol.repository.WeeklyCommitStatsRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
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

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import static com.valb3r.projectcontrol.service.analyze.DateUtil.weekEnd;
import static com.valb3r.projectcontrol.service.analyze.DateUtil.weekStart;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.kie.internal.io.ResourceFactory.newByteArrayResource;

@Order(1)
@Service
public class ChurnAnalyzer extends CommitBasedAnalyzer implements AnalysisStep {

    private final AliasRepository aliases;
    private final WeeklyCommitStatsRepository commitStatsRepo;
    private final FileInclusionRuleRepository inclusionRepo;
    private final FileExclusionRuleRepository exclusionRepo;

    public ChurnAnalyzer(GitRepoRepository gitRepoRepository, AliasRepository aliases, WeeklyCommitStatsRepository commitStatsRepo,
                         FileInclusionRuleRepository inclusionRepo, FileExclusionRuleRepository exclusionRepo) {
        super(gitRepoRepository);
        this.aliases = aliases;
        this.commitStatsRepo = commitStatsRepo;
        this.inclusionRepo = inclusionRepo;
        this.exclusionRepo = exclusionRepo;
    }

    @Override
    public GitRepo.AnalysisState stateOnStart() {
        return GitRepo.AnalysisState.CHURN_COUNTING;
    }

    @Override
    public GitRepo.AnalysisState stateOnSuccess() {
        return GitRepo.AnalysisState.CHURN_COUNTED;
    }

    @Override
    protected void processCommit(Git git, GitRepo repo, HashMap<String, Alias> aliasCache, RevWalk walk, KieContainer container, RevCommit commit) {
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

        var stat = commitStatsRepo.findBy(alias.getId(), weekStart(analyzed.getDate()))
                .orElseGet(() ->
                        WeeklyCommitStats.builder()
                                .alias(alias)
                                .repo(repo)
                                .from(weekStart(analyzed.getDate()))
                                .to(weekEnd(analyzed.getDate()))
                                .build()
                );

        stat.setLinesRemoved(stat.getLinesRemoved() + analyzed.getLinesDeleted());
        stat.setLinesAdded(stat.getLinesAdded() + analyzed.getLinesAdded());
        stat.setCommitCount(stat.getCommitCount() + 1);
        commitStatsRepo.save(stat);
    }

    @Override
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

    @SneakyThrows
    private CommitStat analyzeCommit(AnalysisCtx ctx) {
        try (DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
            df.setRepository(ctx.getRepo());
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);

            List<DiffEntry> diffs = computeDiffEntries(ctx, df);
            var stateless = ctx.getContainer().newStatelessKieSession();

            long linesDeleted = 0;
            long linesAdded = 0;

            for (var diff : diffs) {
                var context = new ChurnContext(diff.getNewPath(), ctx.getCommit().getAuthorIdent().getName(), ctx.getCommit().getAuthorIdent().getWhen().toInstant());
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

    private List<DiffEntry> computeDiffEntries(AnalysisCtx ctx, DiffFormatter df) throws IOException {
        List<DiffEntry> diffs;
        if (ctx.getCommit().getParentCount() > 0) {
            RevCommit parent = ctx.getRepo().parseCommit(ctx.getCommit().getParent(0).getId());
            diffs = df.scan(parent.getTree(), ctx.getCommit().getTree());
        } else {
            diffs = df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, ctx.getWalk().getObjectReader(), ctx.getCommit().getTree()));
        }
        return diffs;
    }

    @Data
    @RequiredArgsConstructor
    public static class ChurnContext {
        private final String path;
        private final String author;
        private final Instant commitDate;

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
