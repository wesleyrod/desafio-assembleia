package com.teste.api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.teste.api.domain.topic.Topic;
import com.teste.api.domain.votingSession.VotingSession;
import com.teste.api.domain.votingSession.VotingSessionStatus;
import com.teste.api.domain.votingSession.dto.VotingSessionRequestDTO;
import com.teste.api.domain.votingSession.dto.VotingSessionResponseDTO;
import com.teste.api.exception.ResourceNotFoundException;
import com.teste.api.repositories.TopicRepository;
import com.teste.api.repositories.VotingSessionRepository;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VotingSessionService {

    private final VotingSessionRepository sessionRepository;
    private final TopicRepository topicRepository;

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
}
