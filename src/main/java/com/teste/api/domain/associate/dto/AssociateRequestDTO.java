package com.teste.api.domain.associate.dto;

import jakarta.validation.constraints.NotBlank;

public record AssociateRequestDTO(
        @NotBlank(message = "O CPF é obrigatório") 
        String cpf,
        
        @NotBlank(message = "O nome é obrigatório") 
        String name
) {
}
