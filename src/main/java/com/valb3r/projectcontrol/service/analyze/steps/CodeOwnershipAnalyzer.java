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
        if (null != ctx.getPrevCommit()) {
            if (weekStart(ctx.getPrevCommit().getAuthorIdent().getWhen().toInstant()).equals(weekStart(ctx.getCommit().getAuthorIdent().getWhen().toInstant()))) {
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
                var context = new RuleContext(treeWalk.getPathString(), ctx.getCommit().getAuthorIdent().getName(), ctx.getCommit().getAuthorIdent().getWhen().toInstant());
                stateless.execute(context);

                if (context.isExclude() || (!context.isInclude() && ctx.isHasInclusionRules())) {
                    continue;
                }

                BlameResult result = ctx.getGit().blame().setStartCommit(lastCommitInWeek).setFilePath(treeWalk.getPathString())
                        .setTextComparator(RawTextComparator.WS_IGNORE_ALL).setFollowFileRenames(true).call();
                RawText rawText = result.getResultContents();
                for (int i = 0; i < rawText.size(); i++) {
                    PersonIdent sourceAuthor = result.getSourceAuthor(i);
                    var alias = alias(sourceAuthor.getName(), ctx);
                    owned.compute(alias, (id, value) -> null == value ? 1L : value + 1);
                }
            }
        }

        Instant commitAuthoredTime = lastCommitInWeek.getAuthorIdent().getWhen().toInstant();
        for (var stat : owned.entrySet()) {
            var start = weekStart(commitAuthoredTime);
            var end = weekEnd(commitAuthoredTime);
            var stats = totalOwnershipStatsRepository.findBy(stat.getKey().getId(), start);
            TotalOwnershipStats toSave = null;
            if (stats.isEmpty()) {
                toSave = TotalOwnershipStats.builder()
                        .repo(ctx.getRepo()).alias(stat.getKey())
                        .linesOwned(stat.getValue())
                        .from(start)
                        .to(end)
                        .trueCommitTime(commitAuthoredTime)
                        .build();
                totalOwnershipStatsRepository.save(toSave);
            } else if (stats.get().getTrueCommitTime().isBefore(commitAuthoredTime)){
                toSave = stats.get();
                toSave.setLinesOwned(stat.getValue());
                toSave.setTrueCommitTime(commitAuthoredTime);
                totalOwnershipStatsRepository.save(toSave);
            }

            if (null != toSave) {
                log.debug("OWNERSHIP[{}]: Saved {} of {} with {} owned lines", ctx.getCommit().getId(), start, stat.getKey().getName(), toSave.getLinesOwned());
            } else {
                log.debug("OWNERSHIP[{}]: Discard {} of {}", ctx.getCommit().getId(), start, stat.getKey().getName());
            }
        }
    }
}
