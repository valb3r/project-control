package com.valb3r.projectcontrol.service.analyze.steps;

import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.domain.rules.RuleContext;
import com.valb3r.projectcontrol.domain.stats.TotalOwnershipStats;
import com.valb3r.projectcontrol.repository.AliasRepository;
import com.valb3r.projectcontrol.repository.FileExclusionRuleRepository;
import com.valb3r.projectcontrol.repository.FileInclusionRuleRepository;
import com.valb3r.projectcontrol.repository.TotalOwnershipStatsRepository;
import com.valb3r.projectcontrol.service.analyze.StateUpdatingService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static com.valb3r.projectcontrol.service.analyze.DateUtil.weekEnd;
import static com.valb3r.projectcontrol.service.analyze.DateUtil.weekStart;

/// !!!! IMPORTANT Code ownership is related to commit date and not to authored date
/// Only when commit reaches target branch we can attribute code ownership!
@Slf4j
@Order(2)
@Service
public class CodeOwnershipAnalyzer extends CommitBasedAnalyzer implements AnalysisStep {

    private final TotalOwnershipStatsRepository totalOwnershipStatsRepository;

    public CodeOwnershipAnalyzer(AliasRepository aliases, StateUpdatingService stateUpdatingService, FileInclusionRuleRepository inclusionRepo, FileExclusionRuleRepository exclusionRepo, TotalOwnershipStatsRepository totalOwnershipStatsRepository) {
        super(aliases, stateUpdatingService, inclusionRepo, exclusionRepo);
        this.totalOwnershipStatsRepository = totalOwnershipStatsRepository;
    }

    @Override
    public GitRepo.AnalysisState stateOnStart() {
        return GitRepo.AnalysisState.LOC_OWNERSHIP_COUNTING;
    }

    @Override
    public GitRepo.AnalysisState stateOnSuccess() {
        return GitRepo.AnalysisState.LOC_OWNERSHIP_COUNTED;
    }

    @Override
    @SneakyThrows
    protected void processCommit(CommitCtx ctx) {
        log.debug("OWNERSHIP[{}]: Processing", ctx.getCommit().getId());
        RevCommit lastCommitInWeek = ctx.getCommit();
        Instant commitDate = ctx.getCommit().getCommitterIdent().getWhen().toInstant();
        if (null != ctx.getPrevCommit()) {
            if (weekStart(ctx.getPrevCommit().getCommitterIdent().getWhen().toInstant()).equals(weekStart(commitDate))) {
                log.debug("OWNERSHIP[{}]: Same week as previous", ctx.getCommit().getId());
                return;
            }
        }

        var stateless = ctx.getContainer().newStatelessKieSession();
        Map<Alias, Long> owned = new HashMap<>();
        try (var treeWalk = new TreeWalk(ctx.getGit().getRepository())) {
            var tree = ctx.getWalk().parseTree(lastCommitInWeek.getTree().getId());
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            while (treeWalk.next()) {
                var context = new RuleContext(treeWalk.getPathString(), ctx.getCommit().getAuthorIdent().getName(), commitDate);
                stateless.execute(context);

                if (context.isExclude() || (!context.isInclude() && ctx.isHasInclusionRules())) {
                    continue;
                }

                BlameResult result = ctx.getGit().blame().setStartCommit(lastCommitInWeek).setFilePath(treeWalk.getPathString())
                        .setTextComparator(RawTextComparator.WS_IGNORE_ALL).setFollowFileRenames(true).call();

                if (null == result) {
                    log.warn("Null blame result for {}@{}", treeWalk.getPathString(), lastCommitInWeek);
                    continue;
                }

                RawText rawText = result.getResultContents();
                for (int i = 0; i < rawText.size(); i++) {
                    PersonIdent sourceAuthor = result.getSourceAuthor(i);
                    var alias = alias(sourceAuthor.getName(), ctx);
                    owned.compute(alias, (id, value) -> null == value ? 1L : value + 1);
                }
            }
        }

        /// !!!! IMPORTANT Code ownership is related to commit date and not to authored date
        /// Only when commit reaches target branch we can attribute code ownership!
        Instant attributionDate = lastCommitInWeek.getCommitterIdent().getWhen().toInstant();
        for (var stat : owned.entrySet()) {
            var start = weekStart(attributionDate);
            var end = weekEnd(attributionDate);
            var stats = totalOwnershipStatsRepository.findBy(stat.getKey().getId(), start)
                    .orElseGet(() -> TotalOwnershipStats.builder().repo(ctx.getRepo()).alias(stat.getKey()).linesOwned(0L).from(start).to(end).build());
            stats.setLinesOwned(Math.max(stat.getValue(), stats.getLinesOwned()));
            totalOwnershipStatsRepository.save(stats);
            log.debug("OWNERSHIP[{}]: Saved {} of {} with {} owned lines", ctx.getCommit().getId(), start, stat.getKey().getName(), stats.getLinesOwned());
        }
    }
}
