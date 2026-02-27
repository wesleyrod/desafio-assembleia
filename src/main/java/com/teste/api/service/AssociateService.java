package com.teste.api.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teste.api.domain.associate.Associate;
import com.teste.api.domain.associate.dto.AssociateRequestDTO;
import com.teste.api.domain.associate.dto.AssociateResponseDTO;
import com.teste.api.exception.BusinessRulesException;
import com.teste.api.exception.ResourceNotFoundException;
import com.teste.api.repositories.AssociateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssociateService {

    private final AssociateRepository associateRepository;

    @Transactional
    public AssociateResponseDTO create(AssociateRequestDTO request) {
        if (associateRepository.existsByCpf(request.cpf())) {
            throw new BusinessRulesException("Já existe um associado cadastrado com este CPF.");
        }

        Associate associate = new Associate();
        associate.setCpf(request.cpf());
        associate.setName(request.name());
        
        associate = associateRepository.save(associate);
        return new AssociateResponseDTO(associate);
    }

    @Transactional(readOnly = true)
    public List<AssociateResponseDTO> findAll() {
        return associateRepository.findAll()
                .stream()
                .map(AssociateResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AssociateResponseDTO findById(UUID id) {
        Associate associate = associateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Associado não encontrado."));
        return new AssociateResponseDTO(associate);
    }

    @Transactional
    public AssociateResponseDTO update(UUID id, AssociateRequestDTO request) {
        Associate associate = associateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Associado não encontrado para atualização."));

        if (!associate.getCpf().equals(request.cpf()) && associateRepository.existsByCpf(request.cpf())) {
            throw new BusinessRulesException("Já existe outro associado cadastrado com este CPF.");
        }

        associate.setCpf(request.cpf());
        associate.setName(request.name());
        
        associate = associateRepository.save(associate);
        return new AssociateResponseDTO(associate);
    }

    @Transactional
    public void delete(UUID id) {
        Associate associate = associateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Associado não encontrado para exclusão."));
        
        associateRepository.delete(associate);
    }
}
