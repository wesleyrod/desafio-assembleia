package com.teste.api.infra.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.teste.api.domain.votingSession.dto.VotingResultResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class VotingResultProducer {

    private final KafkaTemplate<String, VotingResultResponseDTO> kafkaTemplate;
    
    private static final String TOPIC = "voting-session-results.v1";

    public void sendResult(VotingResultResponseDTO result) {
        log.info("A enviar resultado da sessão [{}] para o tópico do Kafka...", result.sessionId());
        
        kafkaTemplate.send(TOPIC, result.sessionId().toString(), result);
        
        log.info("Mensagem enviada com sucesso para o Kafka!");
    }
}
