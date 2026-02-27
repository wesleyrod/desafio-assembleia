package com.teste.api.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teste.api.domain.session.VotingSession;
import com.teste.api.domain.session.VotingSessionStatus;

@Repository
public interface VotingSessionRepository extends JpaRepository<VotingSession, UUID> {
    
    List<VotingSessionStatus> findByStatusAndClosingDateBefore(VotingSessionStatus status, LocalDateTime date);
    
}
