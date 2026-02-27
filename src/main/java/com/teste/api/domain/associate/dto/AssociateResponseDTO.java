package com.teste.api.domain.associate.dto;

import java.util.UUID;

import com.teste.api.domain.associate.Associate;

public record AssociateResponseDTO(
        UUID id,
        String cpf,
        String name
) {
    public AssociateResponseDTO(Associate associate) {
        this(associate.getId(), associate.getCpf(), associate.getName());
    }
}