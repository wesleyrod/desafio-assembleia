package com.teste.api.domain.votingSession.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SessionRequest(
        @NotNull(message = "O ID da pauta (topic) é obrigatório")
        UUID topicId,

        @Positive(message = "A duração deve ser maior que zero")
        Integer durationInMinutes
) {
}
