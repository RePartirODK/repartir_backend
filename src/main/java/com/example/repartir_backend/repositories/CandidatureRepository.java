package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.CandidatureOffre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidatureRepository extends JpaRepository<CandidatureOffre, Integer> {
}
