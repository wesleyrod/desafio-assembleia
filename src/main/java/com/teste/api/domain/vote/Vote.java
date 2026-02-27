package com.teste.api.domain.vote;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.teste.api.domain.associate.Associate;
import com.teste.api.domain.session.VotingSession;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "vote", uniqueConstraints = {
    @UniqueConstraint(name = "uk_vote_session_associate", 
    columnNames = {"session_id", "associate_id"})
})
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "voting_session_id", nullable = false)
    private VotingSession votingSession;

    @ManyToOne
    @JoinColumn(name = "associate_id", nullable = false)
    private Associate associate;

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_choice", nullable = false)
    private VoteChoice voteChoice;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
