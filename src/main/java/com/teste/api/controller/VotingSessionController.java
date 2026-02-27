package com.teste.api.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.teste.api.domain.votingSession.dto.SessionRequestDTO;
import com.teste.api.domain.votingSession.dto.SessionResponseDTO;
import com.teste.api.service.VotingSessionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/voting-sessions")
@RequiredArgsConstructor
public class VotingSessionController {

    private final VotingSessionService service;

    @PostMapping
    public ResponseEntity<SessionResponseDTO> openVotingSession(
            @RequestBody @Valid SessionRequestDTO request, 
            UriComponentsBuilder uriBuilder) {
        
        SessionResponseDTO response = service.openVotingSession(request);
        
        URI uri = uriBuilder.path("/v1/voting-sessions/{id}").buildAndExpand(response.id()).toUri();
        
        return ResponseEntity.created(uri).body(response);
    }
}
