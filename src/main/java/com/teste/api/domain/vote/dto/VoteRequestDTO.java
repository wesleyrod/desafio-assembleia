package com.teste.api.domain.vote.dto;

import java.util.UUID;

import com.teste.api.domain.vote.VoteChoice;

import jakarta.validation.constraints.NotNull;

public record VoteRequestDTO(
        @NotNull(message = "O ID da sessão é obrigatório") 
        UUID sessionId,
        
        @NotNull(message = "O ID do associado é obrigatório") 
        UUID associateId,
        
        @NotNull(message = "O voto é obrigatório (SIM ou NAO)") 
        VoteChoice voteChoice
) {
}
