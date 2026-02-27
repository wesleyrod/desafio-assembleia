package com.teste.api.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.teste.api.domain.topic.dto.TopicRequestDTO;
import com.teste.api.domain.topic.dto.TopicResponseDTO;
import com.teste.api.service.TopicService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @PostMapping
    public ResponseEntity<TopicResponseDTO> createTopic(
            @RequestBody @Valid TopicRequestDTO request, 
            UriComponentsBuilder uriBuilder) {
        
        TopicResponseDTO response = topicService.createTopic(request);
        
        URI uri = uriBuilder.path("/v1/topics/{id}").buildAndExpand(response.id()).toUri();
        
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TopicResponseDTO>> findAll() {
        return ResponseEntity.ok(topicService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(topicService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicResponseDTO> update(
            @PathVariable UUID id, 
            @RequestBody @Valid TopicRequestDTO request) {
        
        TopicResponseDTO response = topicService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        topicService.delete(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content (padrão REST para deleção)
    }
}
