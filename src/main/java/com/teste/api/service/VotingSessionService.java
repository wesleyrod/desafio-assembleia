package com.teste.api.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.teste.api.domain.topic.Topic;
import com.teste.api.domain.votingSession.VotingSession;
import com.teste.api.domain.votingSession.VotingSessionStatus;
import com.teste.api.domain.votingSession.dto.SessionRequestDTO;
import com.teste.api.domain.votingSession.dto.SessionResponseDTO;
import com.teste.api.repositories.TopicRepository;
import com.teste.api.repositories.VotingSessionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VotingSessionService {

    private final VotingSessionRepository sessionRepository;
    private final TopicRepository topicRepository;

    @Transactional
    public SessionResponseDTO openVotingSession(SessionRequestDTO request) {
        Topic topic = topicRepository.findById(request.topicId())
                .orElseThrow(() -> new IllegalArgumentException("Pauta não encontrada."));

        int duration = (request.durationInMinutes() != null) ? request.durationInMinutes() : 1;
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime closingDate = now.plusMinutes(duration);

        VotingSession session = new VotingSession();
        session.setTopic(topic);
        session.setClosingDate(closingDate);
        session.setStatus(VotingSessionStatus.OPEN);

        session = sessionRepository.save(session);

        return new SessionResponseDTO(session);
    }
}
