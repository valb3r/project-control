package com.valb3r.projectcontrol.service.analyze.steps;

import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.domain.stats.TotalOwnershipStats;
import com.valb3r.projectcontrol.repository.AliasRepository;
import com.valb3r.projectcontrol.repository.FileExclusionRuleRepository;
import com.valb3r.projectcontrol.repository.FileInclusionRuleRepository;
import com.valb3r.projectcontrol.repository.TotalOwnershipStatsRepository;
import com.valb3r.projectcontrol.service.analyze.StateUpdatingService;
import lombok.SneakyThrows;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.valb3r.projectcontrol.service.analyze.DateUtil.weekEnd;
import static com.valb3r.projectcontrol.service.analyze.DateUtil.weekStart;

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
        RevCommit target = ctx.getCommit();
        if (null != ctx.getPrevCommit()) {
            if (weekStart(ctx.getPrevCommit().getAuthorIdent().getWhen().toInstant()).equals(weekStart(ctx.getCommit().getAuthorIdent().getWhen().toInstant()))) {
                return;
            }

            target = ctx.getPrevCommit();
        }

        var stateless = ctx.getContainer().newStatelessKieSession();
        Map<Alias, Long> owned = new HashMap<>();
        try (var treeWalk = new TreeWalk(ctx.getGit().getRepository())) {
            var tree = ctx.getWalk().parseTree(target.getTree().getId());
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            while (treeWalk.next()) {
                var context = new RuleContext(treeWalk.getPathString(), ctx.getCommit().getAuthorIdent().getName(), ctx.getCommit().getAuthorIdent().getWhen().toInstant());
                stateless.execute(context);

                if (context.isExclude() || !context.isInclude()) {
                    continue;
                }

                BlameResult result = ctx.getGit().blame().setStartCommit(target).setFilePath(treeWalk.getPathString())
                        .setTextComparator(RawTextComparator.WS_IGNORE_ALL).call();
                RawText rawText = result.getResultContents();
                for (int i = 0; i < rawText.size(); i++) {
                    PersonIdent sourceAuthor = result.getSourceAuthor(i);
                    var alias = alias(sourceAuthor.getName(), ctx);
                    owned.compute(alias, (id, value) -> null == value ? 1L : value + 1);
                }
            }
        }

        for (var stat : owned.entrySet()) {
            var start = weekStart(target.getAuthorIdent().getWhen().toInstant());
            var end = weekEnd(target.getAuthorIdent().getWhen().toInstant());
            var stats = totalOwnershipStatsRepository.findBy(stat.getKey().getId(), start)
                    .orElseGet(() -> TotalOwnershipStats.builder().repo(ctx.getRepo()).alias(stat.getKey()).linesOwned(0L).from(start).to(end).build());
            stats.setLinesOwned(stat.getValue());
            totalOwnershipStatsRepository.save(stats);
        }
    }
}
