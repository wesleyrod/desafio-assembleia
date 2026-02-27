package com.teste.api.domain.topic.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.teste.api.domain.topic.Topic;

public record TopicResponseDTO(
        UUID id,
        String description,
        LocalDateTime createdAt
) {
    public TopicResponseDTO(Topic topic) {
        this(topic.getId(), topic.getDescription(), topic.getCreatedAt());
    }
}
