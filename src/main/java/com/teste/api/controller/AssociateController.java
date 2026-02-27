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

import com.teste.api.domain.associate.dto.AssociateRequestDTO;
import com.teste.api.domain.associate.dto.AssociateResponseDTO;
import com.teste.api.service.AssociateService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/associates")
@RequiredArgsConstructor

public class AssociateController {

    private final AssociateService associateService;

    @PostMapping
    public ResponseEntity<AssociateResponseDTO> create(
            @RequestBody @Valid AssociateRequestDTO request, 
            UriComponentsBuilder uriBuilder) {
        
        AssociateResponseDTO response = associateService.create(request);
        URI uri = uriBuilder.path("/v1/associates/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AssociateResponseDTO>> findAll() {
        return ResponseEntity.ok(associateService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssociateResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(associateService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssociateResponseDTO> update(
            @PathVariable UUID id, 
            @RequestBody @Valid AssociateRequestDTO request) {
        
        return ResponseEntity.ok(associateService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        associateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
