package com.teste.api.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.teste.api.domain.vote.dto.VoteRequestDTO;
import com.teste.api.domain.vote.dto.VoteResponseDTO;
import com.teste.api.service.VoteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<VoteResponseDTO> registerVote(
            @RequestBody @Valid VoteRequestDTO request, 
            UriComponentsBuilder uriBuilder) {
        
        VoteResponseDTO response = voteService.registerVote(request);
        
        URI uri = uriBuilder.path("/v1/votes/{id}").buildAndExpand(response.id()).toUri();
        
        return ResponseEntity.created(uri).body(response);
    }
}
