package com.valb3r.projectcontrol.service.analyze.steps;

import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.domain.rules.RuleContext;
import com.valb3r.projectcontrol.domain.stats.RemovedLines;
import com.valb3r.projectcontrol.domain.stats.WeeklyCommitStats;
import com.valb3r.projectcontrol.repository.AliasRepository;
import com.valb3r.projectcontrol.repository.FileExclusionRuleRepository;
import com.valb3r.projectcontrol.repository.FileInclusionRuleRepository;
import com.valb3r.projectcontrol.repository.RemovedLinesRepository;
import com.valb3r.projectcontrol.repository.WeeklyCommitStatsRepository;
import com.valb3r.projectcontrol.service.analyze.StateUpdatingService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.valb3r.projectcontrol.service.analyze.DateUtil.weekEnd;
import static com.valb3r.projectcontrol.service.analyze.DateUtil.weekStart;

@Slf4j
@Order(1)
@Service
public class ChurnAnalyzer extends CommitBasedAnalyzer {

    private final WeeklyCommitStatsRepository commitStatsRepo;
    private final RemovedLinesRepository removedLinesRepo;

    public ChurnAnalyzer(AliasRepository aliases, StateUpdatingService stateUpdatingService, FileInclusionRuleRepository inclusionRepo, FileExclusionRuleRepository exclusionRepo, WeeklyCommitStatsRepository commitStatsRepo, RemovedLinesRepository removedLinesRepo) {
        super(aliases, stateUpdatingService, inclusionRepo, exclusionRepo);
        this.commitStatsRepo = commitStatsRepo;
        this.removedLinesRepo = removedLinesRepo;
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
        log.debug("CHURN[{}]: Processing", ctx.getCommit().getId());
        var name = ctx.getCommit().getAuthorIdent().getName();
        var alias = alias(name, ctx);
        var analyzed = analyzeCommit(
                ctx,
                AnalysisCtx.builder()
                        .container(ctx.getContainer())
                        .repo(ctx.getGit().getRepository())
                        .commit(ctx.getCommit())
                        .walk(ctx.getWalk())
                        .build()
        );

        var stat = weeklyStat(ctx, alias, analyzed);

        stat.setLinesRemoved(stat.getLinesRemoved() + analyzed.getLinesDeleted());
        stat.setLinesAdded(stat.getLinesAdded() + analyzed.getLinesAdded());
        stat.setCommitCount(stat.getCommitCount() + 1);
        analyzed.getOtherOwnersRemovedLines().forEach((removedAlias, linesRemoved) -> {
            var removed = removedLinesRepo.findByWeeklyIdAndFromAuthorId(stat.getId(), removedAlias.getId())
                    .orElseGet(() -> RemovedLines.builder().fromAuthor(removedAlias).weekly(stat).removedLines(0L).build());
            removed.setRemovedLines(removed.getRemovedLines() + linesRemoved);
            removedLinesRepo.save(removed);
        });

        commitStatsRepo.save(stat);
        log.debug("CHURN[{}]: Saved {} of {} with churn {} (+{} -{})", ctx.getCommit().getId(), weekStart(analyzed.getDate()),
                alias.getName(), stat.getLinesAdded() + stat.getLinesRemoved(), stat.getLinesAdded(), stat.getLinesRemoved());
    }

    private WeeklyCommitStats weeklyStat(CommitCtx ctx, Alias alias, CommitStat analyzed) {
        return commitStatsRepo.findBy(alias.getId(), weekStart(analyzed.getDate()))
                .orElseGet(() ->
                        commitStatsRepo.save(WeeklyCommitStats.builder()
                                .alias(alias)
                                .repo(ctx.getRepo())
                                .from(weekStart(analyzed.getDate()))
                                .to(weekEnd(analyzed.getDate()))
                                .build()
                        )
                );
    }

    @SneakyThrows
    private CommitStat analyzeCommit(CommitCtx commitCtx, AnalysisCtx ctx) {
        try (DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
            df.setRepository(ctx.getRepo());
            df.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
            df.setDetectRenames(true);

            List<DiffEntry> diffs = computeDiffEntries(ctx, df);
            var stateless = ctx.getContainer().newStatelessKieSession();

            long linesDeleted = 0;
            long linesAdded = 0;
            Map<Alias, Long> stat = new HashMap<>();
            for (var diff : diffs) {
                var context = new RuleContext(diff.getNewPath(), ctx.getCommit().getAuthorIdent().getName(), ctx.getCommit().getAuthorIdent().getWhen().toInstant());
                stateless.execute(context);

                if (context.isExclude() || (!context.isInclude() && commitCtx.isHasInclusionRules())) {
                    continue;
                }

                AtomicReference<BlameResult> reeditContext = new AtomicReference<>();
                for (Edit edit : df.toFileHeader(diff).toEditList()) {
                    linesDeleted += edit.getEndA() - edit.getBeginA();
                    linesAdded += edit.getEndB() - edit.getBeginB();

                    if (linesDeleted > 0) {
                        reeditsStats(commitCtx, reeditContext, edit, diff, stat);
                    }
                }
            }

            return new CommitStat(linesAdded, linesDeleted, ctx.getCommit().getAuthorIdent().getWhen().toInstant(), stat);
        }
    }

    @SneakyThrows
    private void reeditsStats(CommitCtx commitCtx, AtomicReference<BlameResult> reeditContext, Edit edit, DiffEntry entry, Map<Alias, Long> stat) {
        if (null == commitCtx.getPrevCommit()) {
            return;
        }

        var blameResult = reeditContext.updateAndGet(reedit -> getReeditContext(commitCtx, entry, reedit));
        if (null == blameResult) {
            return;
        }

        var result = blameResult.getResultContents();
        for (int i = 0; i < result.size(); i++) {
            if (!(blameResult.getSourceLine(i) >= edit.getBeginA() && blameResult.getSourceLine(i) <= edit.getEndA())) {
                continue;
            }

            PersonIdent sourceAuthor = blameResult.getSourceAuthor(i);
            var alias = alias(sourceAuthor.getName(), commitCtx);
            stat.compute(alias, (id, lines) -> null == lines ? 1L : lines + 1);
        }
    }

    @SneakyThrows
    private BlameResult getReeditContext(CommitCtx ctx, DiffEntry entry, BlameResult reedit) {
        if (null != reedit) {
            return reedit;
        }

        return ctx.getGit().blame().setStartCommit(ctx.getPrevCommit()).setFilePath(entry.getNewPath())
                .setTextComparator(RawTextComparator.WS_IGNORE_ALL).call();
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

    @Getter
    @Builder(toBuilder = true)
    @AllArgsConstructor
    private static class CommitStat {

        private final long linesAdded;
        private final long linesDeleted;
        private final Instant date;

        @Builder.Default
        private final Map<Alias, Long> otherOwnersRemovedLines = new HashMap<>();
    }
}
