package com.teste.api.domain.votingSession.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.teste.api.domain.votingSession.VotingSession;

public record VotingSessionResponseDTO(
        UUID id,
        UUID topicId,
        LocalDateTime openingDate,
        LocalDateTime closingDate,
        String status
) {
    public VotingSessionResponseDTO(VotingSession session) {
        this(
                session.getId(),
                session.getTopic().getId(),
                session.getOpeningDate(),
                session.getClosingDate(),
                session.getStatus().name()
        );
    }
}
