package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.OffreEmploi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OffreEmploiRepository extends JpaRepository<OffreEmploi, Integer> {
    List<OffreEmploi> findByEntrepriseId(int entrepriseId);
}
