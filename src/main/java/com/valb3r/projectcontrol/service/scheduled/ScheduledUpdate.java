package com.valb3r.projectcontrol.service.scheduled;

import com.valb3r.projectcontrol.domain.GitRepo;
import com.valb3r.projectcontrol.repository.GitRepoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "schedule.reanalyze.enabled", havingValue = "true")
@RequiredArgsConstructor
public class ScheduledUpdate {

    private final GitRepoRepository repositories;

    @Scheduled(cron = "${schedule.reanalyze.cron}")
    public void reanalyze() {
        repositories.findAll().forEach(it -> {
            it.setAnalysisState(GitRepo.AnalysisState.STARTED);
            repositories.save(it);
        });
    }
}
