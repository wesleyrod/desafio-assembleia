package com.teste.api.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teste.api.domain.vote.Vote;
import com.teste.api.domain.vote.VoteChoice;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {
    
    boolean existsByVotingSessionIdAndAssociateId(UUID votingSessionId, UUID associateId);
    long countBySessionId(UUID sessionId);
    long countBySessionIdAndVoteChoice(UUID sessionId, VoteChoice voteChoice);
    
}
