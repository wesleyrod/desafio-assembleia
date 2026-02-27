package com.teste.api.domain.topic.dto;

import jakarta.validation.constraints.NotBlank;

public record TopicRequestDTO(
        @NotBlank(message = "A descrição da pauta é obrigatória e não pode estar em branco") 
        String description
) {
}
