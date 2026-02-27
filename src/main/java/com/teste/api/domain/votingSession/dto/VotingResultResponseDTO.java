package com.teste.api.domain.votingSession.dto;

import java.util.UUID;

public record VotingResultResponseDTO(
        UUID sessionId,
        UUID topicId,
        long totalVotes,
        long totalSim,
        long totalNao,
        String finalResult
) {
}
  
