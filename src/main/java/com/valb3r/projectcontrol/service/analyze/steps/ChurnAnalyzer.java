package com.valb3r.projectcontrol.service.analyze.steps;

import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.domain.stats.WeeklyCommitStats;
import com.valb3r.projectcontrol.repository.AliasRepository;
import com.valb3r.projectcontrol.repository.FileExclusionRuleRepository;
import com.valb3r.projectcontrol.repository.FileInclusionRuleRepository;
import com.valb3r.projectcontrol.repository.WeeklyCommitStatsRepository;
import com.valb3r.projectcontrol.service.analyze.StateUpdatingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.revwalk.RevCommit;
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
import java.util.List;

import static com.valb3r.projectcontrol.service.analyze.DateUtil.weekEnd;
import static com.valb3r.projectcontrol.service.analyze.DateUtil.weekStart;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.kie.internal.io.ResourceFactory.newByteArrayResource;

@Order(1)
@Service
public class ChurnAnalyzer extends CommitBasedAnalyzer implements AnalysisStep {

    private final WeeklyCommitStatsRepository commitStatsRepo;

    public ChurnAnalyzer(AliasRepository aliases, StateUpdatingService stateUpdatingService, FileInclusionRuleRepository inclusionRepo, FileExclusionRuleRepository exclusionRepo, WeeklyCommitStatsRepository commitStatsRepo) {
        super(aliases, stateUpdatingService, inclusionRepo, exclusionRepo);
        this.commitStatsRepo = commitStatsRepo;
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
    protected void processCommit(CommitCtx ctx) {
        var name = ctx.getCommit().getAuthorIdent().getName();
        var alias = alias(name, ctx);
        var analyzed = analyzeCommit(
                AnalysisCtx.builder()
                        .container(ctx.getContainer())
                        .repo(ctx.getGit().getRepository())
                        .commit(ctx.getCommit())
                        .walk(ctx.getWalk())
                        .build()
        );

        var stat = commitStatsRepo.findBy(alias.getId(), weekStart(analyzed.getDate()))
                .orElseGet(() ->
                        WeeklyCommitStats.builder()
                                .alias(alias)
                                .repo(ctx.getRepo())
                                .from(weekStart(analyzed.getDate()))
                                .to(weekEnd(analyzed.getDate()))
                                .build()
                );

        stat.setLinesRemoved(stat.getLinesRemoved() + analyzed.getLinesDeleted());
        stat.setLinesAdded(stat.getLinesAdded() + analyzed.getLinesAdded());
        stat.setCommitCount(stat.getCommitCount() + 1);
        commitStatsRepo.save(stat);
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
                var context = new RuleContext(diff.getNewPath(), ctx.getCommit().getAuthorIdent().getName(), ctx.getCommit().getAuthorIdent().getWhen().toInstant());
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
    private static class CommitStat {

        private final long linesAdded;
        private final long linesDeleted;
        private final Instant date;
    }
}
