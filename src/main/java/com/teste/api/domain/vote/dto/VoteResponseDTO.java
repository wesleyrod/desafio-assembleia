package com.teste.api.domain.vote.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.teste.api.domain.vote.Vote;

public record VoteResponseDTO(
        UUID id,
        UUID sessionId,
        UUID associateId,
        String voteChoice,
        LocalDateTime createdAt
) {
    public VoteResponseDTO(Vote vote) {
        this(
            vote.getId(),
            vote.getVotingSession().getId(),
            vote.getAssociate().getId(),
            vote.getVoteChoice().name(),
            vote.getCreatedAt()
        );
    }
}
