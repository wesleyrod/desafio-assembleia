package com.teste.api.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teste.api.domain.associate.Associate;

@Repository
public interface AssociateRepository extends JpaRepository<Associate, UUID> {
    Optional<Associate> findByCpf(String cpf);
    
}