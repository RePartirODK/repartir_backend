package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Jeune;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JeuneRepository extends JpaRepository<Jeune, Integer> {
    void deleteByUtilisateurId(int id);
}
