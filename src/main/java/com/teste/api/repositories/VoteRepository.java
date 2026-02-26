package com.teste.api.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teste.api.domain.vote.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {
    
    boolean existsBySessionIdAndAssociateId(UUID sessionId, UUID associateId);
    
}
