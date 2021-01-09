package com.valb3r.projectcontrol.service.analyze;

import com.valb3r.projectcontrol.config.GitConfig;
import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.repository.AliasRepository;
import com.valb3r.projectcontrol.repository.FileExclusionRuleRepository;
import com.valb3r.projectcontrol.repository.FileInclusionRuleRepository;
import com.valb3r.projectcontrol.repository.GitRepoRepository;
import com.valb3r.projectcontrol.service.analyze.steps.AnalysisStep;
import com.valb3r.projectcontrol.service.analyze.steps.CloneRepoStep;
import com.valb3r.projectcontrol.service.analyze.steps.CommitBasedAnalyzer;
import com.valb3r.projectcontrol.service.analyze.steps.CommitCtx;
import lombok.SneakyThrows;
import net.lingala.zip4j.ZipFile;
import org.assertj.core.api.iterable.ThrowingExtractor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.support.TransactionOperations;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class RepoAnalyzerTest {

    private final AtomicReference<GitRepo> repo = new AtomicReference<>();

    private GitRepoRepository gitRepoRepository = mock(GitRepoRepository.class);
    private TransactionOperations operations = mock(TransactionOperations.class);

    @Mock
    private GitConfig config;

    @Mock
    private Repository repository;

    @Mock
    private CloneRepoStep cloneRepoStep;

    @Mock
    private AliasRepository aliases;


    @Mock
    private FileInclusionRuleRepository inclusionRepo;

    @Mock
    private FileExclusionRuleRepository exclusionRepo;

    @Spy
    private StateUpdatingService stateUpdatingService = new StateUpdatingService(gitRepoRepository, operations);

    @Spy
    @InjectMocks
    private ChurnAnalyzerTestable commitAnalyzer;

    @Spy
    @InjectMocks
    private CodeOwnershipAnalyzerTestable codeOwnershipAnalyzer;

    private RepoAnalyzer tested;

    private ArgumentCaptor<CommitCtx> commitAnalyzerCaptor = ArgumentCaptor.forClass(CommitCtx.class);
    private ArgumentCaptor<CommitCtx> ownershipAnalyzerCaptor = ArgumentCaptor.forClass(CommitCtx.class);

    @TempDir
    File repoToAnalyze;

    @BeforeEach
    @SneakyThrows
    void init() {
        var resource = new ClassPathResource("repos/simple-repo.zip");
        new ZipFile(resource.getFile()).extractAll(repoToAnalyze.getAbsolutePath());

        initMocks(this);
        var gitRepo = new GitRepo();
        repo.set(gitRepo);
        gitRepo.setBranchToAnalyze("master");
        when(cloneRepoStep.execute(any(), any())).thenReturn(Git.open(repoToAnalyze));
        when(cloneRepoStep.stateOnStart()).thenReturn(GitRepo.AnalysisState.CLONING);
        when(cloneRepoStep.stateOnSuccess()).thenReturn(GitRepo.AnalysisState.CLONED);
        tested = new RepoAnalyzer(stateUpdatingService, List.of(cloneRepoStep, commitAnalyzer, codeOwnershipAnalyzer));

        when(gitRepoRepository.save(any())).thenAnswer(inv -> {
            var toSave = inv.getArgumentAt(0, GitRepo.class);
            repo.set(toSave);
            return toSave;
        });

        doAnswer(inv -> {
            inv.getArgumentAt(0, Consumer.class).accept(null);
            return null;
        }).when(operations).executeWithoutResult(any());
    }

    @Test
    void testNewAnalysisOnRepository() {
        tested.analyze(repo.get());

        verify(cloneRepoStep).execute(any(), any());
        var order = inOrder(commitAnalyzer, codeOwnershipAnalyzer);
        order.verify(commitAnalyzer, times(4)).processCommit(commitAnalyzerCaptor.capture());
        assertThat(commitAnalyzerCaptor.getAllValues()).map(commitMessageStart()).containsExactly("4", "3", "2", "1");
        order.verify(codeOwnershipAnalyzer, times(4)).processCommit(ownershipAnalyzerCaptor.capture());
        assertThat(ownershipAnalyzerCaptor.getAllValues()).map(commitMessageStart()).containsExactly("4", "3", "2", "1");
    }

    @Test
    @SneakyThrows
    void testAnalysisRestartOnCloneOnRepository() {
        when(cloneRepoStep.execute(any(), any())).thenThrow(new IllegalStateException());
        tested.analyze(repo.get());
        verify(commitAnalyzer, never()).processCommit(any());
        verify(codeOwnershipAnalyzer, never()).processCommit(any());

        doReturn(Git.open(repoToAnalyze)).when(cloneRepoStep).execute(any(), any());
        tested.analyze(repo.get());
        verify(cloneRepoStep, times(2)).execute(any(), any());
        var order = inOrder(commitAnalyzer, codeOwnershipAnalyzer);
        order.verify(commitAnalyzer, times(4)).processCommit(commitAnalyzerCaptor.capture());
        assertThat(commitAnalyzerCaptor.getAllValues()).map(commitMessageStart()).containsExactly("4", "3", "2", "1");
        order.verify(codeOwnershipAnalyzer, times(4)).processCommit(ownershipAnalyzerCaptor.capture());
        assertThat(ownershipAnalyzerCaptor.getAllValues()).map(commitMessageStart()).containsExactly("4", "3", "2", "1");
    }

    @Test
    void testAnalysisRestartOnChurnOnRepository() {
        doAnswer(inv -> {
            var ctx = inv.getArgumentAt(0, CommitCtx.class);
            if (ctx.getCommit().getShortMessage().startsWith("3.")) {
                throw new IllegalStateException();
            }

            return null;
        }).when(commitAnalyzer).processCommit(any());

        tested.analyze(repo.get());
        verify(cloneRepoStep).execute(any(), any());
        var order = inOrder(commitAnalyzer, codeOwnershipAnalyzer);
        order.verify(commitAnalyzer, times(2)).processCommit(commitAnalyzerCaptor.capture());
        assertThat(commitAnalyzerCaptor.getAllValues()).map(commitMessageStart()).containsExactly("4", "3");
        order.verify(codeOwnershipAnalyzer, never()).processCommit(ownershipAnalyzerCaptor.capture());

        commitAnalyzerCaptor = ArgumentCaptor.forClass(CommitCtx.class);
        ownershipAnalyzerCaptor = ArgumentCaptor.forClass(CommitCtx.class);
        doAnswer(inv -> null).when(commitAnalyzer).processCommit(any());
        tested.analyze(repo.get());
        verify(cloneRepoStep, times(2)).execute(any(), any());
        order.verify(commitAnalyzer, times(3)).processCommit(commitAnalyzerCaptor.capture());
        assertThat(commitAnalyzerCaptor.getAllValues()).map(commitMessageStart()).containsExactly("3", "2", "1");
        order.verify(codeOwnershipAnalyzer, times(4)).processCommit(ownershipAnalyzerCaptor.capture());
        assertThat(ownershipAnalyzerCaptor.getAllValues()).map(commitMessageStart()).containsExactly("4", "3", "2", "1");
    }

    @Test
    void testAnalysisRestartOnOwnershipOnRepository() {
        doAnswer(inv -> {
            var ctx = inv.getArgumentAt(0, CommitCtx.class);
            if (ctx.getCommit().getShortMessage().startsWith("3.")) {
                throw new IllegalStateException();
            }

            return null;
        }).when(codeOwnershipAnalyzer).processCommit(any());

        tested.analyze(repo.get());
        verify(cloneRepoStep).execute(any(), any());
        var order = inOrder(commitAnalyzer, codeOwnershipAnalyzer);
        order.verify(commitAnalyzer, times(4)).processCommit(commitAnalyzerCaptor.capture());
        assertThat(commitAnalyzerCaptor.getAllValues()).map(commitMessageStart()).containsExactly("4", "3", "2", "1");
        order.verify(codeOwnershipAnalyzer, times(2)).processCommit(ownershipAnalyzerCaptor.capture());
        assertThat(ownershipAnalyzerCaptor.getAllValues()).map(commitMessageStart()).containsExactly("4", "3");

        ownershipAnalyzerCaptor = ArgumentCaptor.forClass(CommitCtx.class);
        doAnswer(inv -> null).when(codeOwnershipAnalyzer).processCommit(any());
        tested.analyze(repo.get());
        verify(cloneRepoStep, times(2)).execute(any(), any());
        order.verify(commitAnalyzer, never()).processCommit(commitAnalyzerCaptor.capture());
        order.verify(codeOwnershipAnalyzer, times(3)).processCommit(ownershipAnalyzerCaptor.capture());
        assertThat(ownershipAnalyzerCaptor.getAllValues()).map(commitMessageStart()).containsExactly("3", "2", "1");
    }

    @Test
    void testAnalysisRestartOnNoCommitsOnRepository() {
        tested.analyze(repo.get());

        verify(cloneRepoStep).execute(any(), any());
        var order = inOrder(commitAnalyzer, codeOwnershipAnalyzer);
        order.verify(commitAnalyzer, times(4)).processCommit(commitAnalyzerCaptor.capture());
        assertThat(commitAnalyzerCaptor.getAllValues()).map(commitMessageStart()).containsExactly("4", "3", "2", "1");
        order.verify(codeOwnershipAnalyzer, times(4)).processCommit(ownershipAnalyzerCaptor.capture());
        assertThat(ownershipAnalyzerCaptor.getAllValues()).map(commitMessageStart()).containsExactly("4", "3", "2", "1");

        commitAnalyzerCaptor = ArgumentCaptor.forClass(CommitCtx.class);
        ownershipAnalyzerCaptor = ArgumentCaptor.forClass(CommitCtx.class);
        tested.analyze(repo.get());
        verify(cloneRepoStep, times(2)).execute(any(), any());
        order.verify(commitAnalyzer, never()).processCommit(commitAnalyzerCaptor.capture());
        order.verify(codeOwnershipAnalyzer, never()).processCommit(ownershipAnalyzerCaptor.capture());
    }

    @Test
    @SneakyThrows
    void testAnalysisRestartOnNewCommitOnRepository() {
        tested.analyze(repo.get());
        verify(cloneRepoStep).execute(any(), any());
        var order = inOrder(commitAnalyzer, codeOwnershipAnalyzer);
        order.verify(commitAnalyzer, times(4)).processCommit(commitAnalyzerCaptor.capture());
        assertThat(commitAnalyzerCaptor.getAllValues()).map(commitMessageStart()).containsExactly("4", "3", "2", "1");
        order.verify(codeOwnershipAnalyzer, times(4)).processCommit(ownershipAnalyzerCaptor.capture());
        assertThat(ownershipAnalyzerCaptor.getAllValues()).map(commitMessageStart()).containsExactly("4", "3", "2", "1");

        var git = Git.open(repoToAnalyze);
        Files.write(repoToAnalyze.toPath().resolve("new-commit.txt"), "".getBytes(StandardCharsets.UTF_8));
        git.add().addFilepattern("*").call();
        git.commit().setMessage("5. New code-based commit").call();

        commitAnalyzerCaptor = ArgumentCaptor.forClass(CommitCtx.class);
        ownershipAnalyzerCaptor = ArgumentCaptor.forClass(CommitCtx.class);
        tested.analyze(repo.get());
        verify(cloneRepoStep, times(2)).execute(any(), any());
        order.verify(commitAnalyzer, times(1)).processCommit(commitAnalyzerCaptor.capture());
        assertThat(commitAnalyzerCaptor.getAllValues()).map(commitMessageStart()).containsExactly("5");
        order.verify(codeOwnershipAnalyzer, times(1)).processCommit(ownershipAnalyzerCaptor.capture());
        assertThat(ownershipAnalyzerCaptor.getAllValues()).map(commitMessageStart()).containsExactly("5");

    }

    private ThrowingExtractor<CommitCtx, String, RuntimeException> commitMessageStart() {
        return it -> it.getCommit().getShortMessage().split("\\.")[0];
    }

    public static class ChurnAnalyzerTestable extends CommitBasedAnalyzer implements AnalysisStep {


        public ChurnAnalyzerTestable(AliasRepository aliases, StateUpdatingService stateUpdatingService, FileInclusionRuleRepository inclusionRepo, FileExclusionRuleRepository exclusionRepo) {
            super(aliases, stateUpdatingService, inclusionRepo, exclusionRepo);
        }

        @Override
        public void processCommit(CommitCtx ctx) {
            // NOP
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
        protected KieSetup container(GitRepo repo) {
            return new KieSetup(null, false, false);
        }
    }

    public static class CodeOwnershipAnalyzerTestable extends CommitBasedAnalyzer implements AnalysisStep {

        public CodeOwnershipAnalyzerTestable(AliasRepository aliases, StateUpdatingService stateUpdatingService, FileInclusionRuleRepository inclusionRepo, FileExclusionRuleRepository exclusionRepo) {
            super(aliases, stateUpdatingService, inclusionRepo, exclusionRepo);
        }

        @Override
        public void processCommit(CommitCtx ctx) {
            // NOP
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
        protected KieSetup container(GitRepo repo) {
            return new KieSetup(null, false, false);
        }
    }
}