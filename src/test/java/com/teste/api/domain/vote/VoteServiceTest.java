package com.teste.api.domain.vote;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.teste.api.domain.vote.dto.VoteRequestDTO;
import com.teste.api.domain.vote.dto.VoteResponseDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import com.teste.api.domain.associate.Associate;
import com.teste.api.domain.topic.Topic;
import com.teste.api.domain.votingSession.VotingSession;
import com.teste.api.domain.votingSession.VotingSessionStatus;
import com.teste.api.exception.BusinessRulesException;
import com.teste.api.repositories.AssociateRepository;
import com.teste.api.repositories.VoteRepository;
import com.teste.api.repositories.VotingSessionRepository;
import com.teste.api.service.VoteService;
import com.teste.api.infra.client.CpfValidationClient;
    
@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;
    @Mock
    private VotingSessionRepository sessionRepository;
    @Mock
    private AssociateRepository associateRepository;
    @Mock
    private CpfValidationClient cpfValidationClient;

    @InjectMocks
    private VoteService voteService;

    private VotingSession openSession;
    private Associate associate;
    private VoteRequestDTO voteRequest;
    private UUID sessionId;
    private UUID associateId;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();
        associateId = UUID.randomUUID();

        Topic topic = new Topic();
        topic.setId(UUID.randomUUID());

        openSession = new VotingSession();
        openSession.setId(sessionId);
        openSession.setTopic(topic);
        openSession.setStatus(VotingSessionStatus.OPEN);
        openSession.setOpeningDate(LocalDateTime.now().minusMinutes(1));
        openSession.setClosingDate(LocalDateTime.now().plusMinutes(2)); 
        associate = new Associate();
        associate.setId(associateId);
        associate.setCpf("11122233344");

        voteRequest = new VoteRequestDTO(sessionId, associateId, VoteChoice.SIM);
    }

    @Test
    @DisplayName("Deve registrar o voto com sucesso quando todas as validações passarem")
    void registerVote_Success() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(openSession));
        when(associateRepository.findById(associateId)).thenReturn(Optional.of(associate));
        doNothing().when(cpfValidationClient).validateCpf(any()); 
        when(voteRepository.existsByVotingSessionIdAndAssociateId(sessionId, associateId)).thenReturn(false);
        
        Vote savedVote = new Vote();
        savedVote.setId(UUID.randomUUID());
        savedVote.setVotingSession(openSession);
        savedVote.setAssociate(associate);
        savedVote.setVoteChoice(VoteChoice.SIM);
        
        when(voteRepository.save(any(Vote.class))).thenReturn(savedVote);

        VoteResponseDTO response = voteService.registerVote(voteRequest);

        assertNotNull(response);
        assertEquals(VoteChoice.SIM.name(), response.voteChoice());
        verify(voteRepository, times(1)).save(any(Vote.class)); 
    }

    @Test
    @DisplayName("Deve lançar BusinessRulesException ao tentar votar em uma sessão encerrada (Status CLOSED)")
    void registerVote_SessionClosed_ThrowsException() {
        openSession.setStatus(VotingSessionStatus.CLOSED);
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(openSession));

        BusinessRulesException exception = assertThrows(BusinessRulesException.class, () -> {
            voteService.registerVote(voteRequest);
        });

        assertEquals("Esta sessão de votação já está encerrada.", exception.getMessage());
        verify(voteRepository, never()).save(any()); 
    }

    @Test
    @DisplayName("Deve lançar BusinessRulesException ao tentar votar mais de uma vez na mesma pauta")
    void registerVote_AlreadyVoted_ThrowsException() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(openSession));
        when(associateRepository.findById(associateId)).thenReturn(Optional.of(associate));
        
        when(voteRepository.existsByVotingSessionIdAndAssociateId(sessionId, associateId)).thenReturn(true);

        BusinessRulesException exception = assertThrows(BusinessRulesException.class, () -> {
            voteService.registerVote(voteRequest);
        });

        assertEquals("O associado já registrou um voto para esta sessão.", exception.getMessage());
        verify(voteRepository, never()).save(any());
    }
}
