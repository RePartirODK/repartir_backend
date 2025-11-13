package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.CandidatureOffre;
import com.example.repartir_backend.entities.Jeune;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidatureRepository extends JpaRepository<CandidatureOffre, Integer> {
    List<CandidatureOffre> findByJeune(Jeune jeune);
}
