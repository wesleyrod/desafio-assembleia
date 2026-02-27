package com.teste.api.infra.scheduler;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.teste.api.service.VotingSessionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class VotingSessionScheduler {

    private final VotingSessionService votingSessionService;

    @Scheduled(fixedDelay = 60000)
    public void checkAndCloseExpiredSessions() {
        log.debug("Scheduler a verificar sessões de votação expiradas...");
        votingSessionService.closeExpiredSessions();
    }
}