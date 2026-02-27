package com.teste.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teste.api.domain.topic.Topic;
import com.teste.api.domain.topic.dto.TopicRequestDTO;
import com.teste.api.domain.topic.dto.TopicResponseDTO;
import com.teste.api.exception.ResourceNotFoundException;
import com.teste.api.repositories.TopicRepository;

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

    @Transactional(readOnly = true)
    public List<TopicResponseDTO> findAll() {
        return topicRepository.findAll()
                .stream()
                .map(TopicResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TopicResponseDTO findById(UUID id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada."));
        return new TopicResponseDTO(topic);
    }

    @Transactional
    public TopicResponseDTO update(UUID id, TopicRequestDTO request) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada para atualização."));
        
        topic.setDescription(request.description());
        
        topic = topicRepository.save(topic);
        return new TopicResponseDTO(topic);
    }

    @Transactional
    public void delete(UUID id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pauta não encontrada para exclusão."));
        
        topicRepository.delete(topic);
    }
}
