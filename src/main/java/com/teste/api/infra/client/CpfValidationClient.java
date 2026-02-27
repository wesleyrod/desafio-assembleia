package com.teste.api.infra.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.teste.api.exception.BusinessRulesException;

@Component
public class CpfValidationClient {

    private final RestTemplate restTemplate;
    
    private static final String MOCK_API_URL = "https://69a101ba2e82ee536f9ff7cd.mockapi.io/teste/associates/";

    public CpfValidationClient() {
        this.restTemplate = new RestTemplate();
    }

    public void validateCpf(String cpf) {
        try {
            String cleanCpf = cpf.replaceAll("[^0-9]", "");

            ResponseEntity<CpfValidationResponse> response = restTemplate.getForEntity(
                    MOCK_API_URL + cleanCpf,
                    CpfValidationResponse.class
            );

            if (response.getBody() == null || !"ABLE_TO_VOTE".equalsIgnoreCase(response.getBody().status())) {
                throw new BusinessRulesException("O associado não está apto para votar (UNABLE_TO_VOTE).");
            }

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new BusinessRulesException("CPF não encontrado no sistema de validação externo.");
            }
            throw new BusinessRulesException("Erro ao consultar a API de validação de CPF.");
            
        } catch (BusinessRulesException e) {
            throw e;
            
        } catch (Exception e) {
            throw new BusinessRulesException("Serviço de validação de CPF indisponível no momento.");
        }
    }

    private record CpfValidationResponse(String id, String status) {}
}
