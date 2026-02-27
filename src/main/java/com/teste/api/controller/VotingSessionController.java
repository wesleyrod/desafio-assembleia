package com.teste.api.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.teste.api.domain.votingSession.dto.VotingResultResponseDTO;
import com.teste.api.domain.votingSession.dto.VotingSessionRequestDTO;
import com.teste.api.domain.votingSession.dto.VotingSessionResponseDTO;
import com.teste.api.service.VotingSessionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/voting-sessions")
@RequiredArgsConstructor
public class VotingSessionController {

    private final VotingSessionService service;

    @PostMapping
    public ResponseEntity<VotingSessionResponseDTO> openVotingSession(
            @RequestBody @Valid VotingSessionRequestDTO request, 
            UriComponentsBuilder uriBuilder) {
        
        VotingSessionResponseDTO response = service.openVotingSession(request);
        
        URI uri = uriBuilder.path("/v1/voting-sessions/{id}").buildAndExpand(response.id()).toUri();
        
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{id}/result")
        public ResponseEntity<VotingResultResponseDTO> getResult(@PathVariable UUID id) {
            VotingResultResponseDTO response = service.getSessionResult(id);
        return ResponseEntity.ok(response);
    }
}
