package com.teste.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teste.api.domain.associate.Associate;
import com.teste.api.domain.vote.Vote;
import com.teste.api.domain.vote.dto.VoteRequestDTO;
import com.teste.api.domain.vote.dto.VoteResponseDTO;
import com.teste.api.domain.votingSession.VotingSession;
import com.teste.api.domain.votingSession.VotingSessionStatus;
import com.teste.api.exception.BusinessRulesException;
import com.teste.api.exception.ResourceNotFoundException;
import com.teste.api.repositories.AssociateRepository;
import com.teste.api.repositories.VoteRepository;
import com.teste.api.repositories.VotingSessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VotingSessionRepository sessionRepository;
    private final AssociateRepository associateRepository;

    @Transactional
    public VoteResponseDTO registerVote(VoteRequestDTO request) {
        
        VotingSession session = sessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de votação não encontrada."));

        if (session.getStatus() == VotingSessionStatus.CLOSED || 
            LocalDateTime.now().isAfter(session.getClosingDate())) {
            throw new BusinessRulesException("Esta sessão de votação já está encerrada.");
        }

        Associate associate = associateRepository.findById(request.associateId())
                .orElseThrow(() -> new ResourceNotFoundException("Associado não encontrado."));

        
        if (voteRepository.existsByVotingSessionIdAndAssociateId(session.getId(), associate.getId())) {
            throw new BusinessRulesException("O associado já registrou um voto para esta sessão.");
        }

        Vote vote = new Vote();
        vote.setVotingSession(session);
        vote.setAssociate(associate);
        vote.setVoteChoice(request.voteChoice());

        vote = voteRepository.save(vote);

        return new VoteResponseDTO(vote);
    }

    @Transactional (readOnly = true)
    public List<VoteResponseDTO> findAll() {
        return voteRepository.findAll()
                .stream()
                .map(VoteResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VoteResponseDTO findById(UUID id) {
        Vote vote = voteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voto não encontrado."));
        return new VoteResponseDTO(vote);
    }
    
    @Transactional(readOnly = true)
    public List<VoteResponseDTO> findBySessionId(UUID sessionId) {
        return voteRepository.findBySessionId(sessionId)
                .stream()
                .map(VoteResponseDTO::new)
                .collect(Collectors.toList());
    }
}