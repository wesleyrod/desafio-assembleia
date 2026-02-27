package com.teste.api.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.teste.api.domain.topic.Topic;
import com.teste.api.domain.topic.dto.TopicRequestDTO;
import com.teste.api.domain.topic.dto.TopicResponseDTO;
import com.teste.api.repositories.TopicRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    @Transactional
    public TopicResponseDTO createTopic(TopicRequestDTO request) {
        Topic topic = new Topic();
        topic.setDescription(request.description());
        topic.setCreatedAt(LocalDateTime.now());
        topic = topicRepository.save(topic);

        return new TopicResponseDTO(topic);
    }
}
