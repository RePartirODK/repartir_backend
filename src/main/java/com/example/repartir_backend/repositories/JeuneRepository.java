package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Jeune;
import com.example.repartir_backend.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JeuneRepository extends JpaRepository<Jeune, Integer> {
    Optional<Jeune> findByUtilisateur(Utilisateur utilisateur);
    void deleteByUtilisateurId(int id);
}
