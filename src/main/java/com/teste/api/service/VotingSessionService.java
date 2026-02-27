package com.teste.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.teste.api.domain.topic.Topic;
import com.teste.api.domain.vote.VoteChoice;
import com.teste.api.domain.votingSession.VotingSession;
import com.teste.api.domain.votingSession.VotingSessionStatus;
import com.teste.api.domain.votingSession.dto.VotingResultResponseDTO;
import com.teste.api.domain.votingSession.dto.VotingSessionRequestDTO;
import com.teste.api.domain.votingSession.dto.VotingSessionResponseDTO;
import com.teste.api.exception.ResourceNotFoundException;
import com.teste.api.repositories.TopicRepository;
import com.teste.api.repositories.VoteRepository;
import com.teste.api.repositories.VotingSessionRepository;


import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VotingSessionService {

    private final VotingSessionRepository sessionRepository;
    private final TopicRepository topicRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public VotingSessionResponseDTO openVotingSession(VotingSessionRequestDTO request) {
        Topic topic = topicRepository.findById(request.topicId())
        .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada com o ID informado."));

        int duration = (request.durationInMinutes() != null) ? request.durationInMinutes() : 1;
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime closingDate = now.plusMinutes(duration);

        VotingSession session = new VotingSession();
        session.setTopic(topic);
        session.setClosingDate(closingDate);
        session.setStatus(VotingSessionStatus.OPEN);

        session = sessionRepository.save(session);

        return new VotingSessionResponseDTO(session);
    }

    @Transactional
    public void closeExpiredSessions() {
        
        List<VotingSession> expiredSessions = sessionRepository
                .findByStatusAndClosingDateBefore(VotingSessionStatus.OPEN, LocalDateTime.now());

        if (!expiredSessions.isEmpty()) {
            log.info("Foram encontradas {} sessões expiradas para encerrar.", expiredSessions.size());

            expiredSessions.forEach(session -> {
                session.setStatus(VotingSessionStatus.CLOSED);
                
                log.info("Sessão ID {} da Pauta ID {} foi encerrada com sucesso.", session.getId(), session.getTopic().getId());
            });

            sessionRepository.saveAll(expiredSessions);
        }
    }


    @Transactional(readOnly = true)
    public VotingResultResponseDTO getSessionResult(UUID sessionId) {
        VotingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de votação não encontrada."));

        long totalVotes = voteRepository.countBySessionId(sessionId);
        long totalSim = voteRepository.countBySessionIdAndVoteChoice(sessionId, VoteChoice.SIM);
        long totalNao = voteRepository.countBySessionIdAndVoteChoice(sessionId, VoteChoice.NAO);

        String finalResult = "EMPATE";
        if (totalSim > totalNao) {
            finalResult = "APROVADA";
        } else if (totalNao > totalSim) {
            finalResult = "REPROVADA";
        }

        return new VotingResultResponseDTO(
                session.getId(),
                session.getTopic().getId(),
                totalVotes,
                totalSim,
                totalNao,
                finalResult
        );
    }
}
