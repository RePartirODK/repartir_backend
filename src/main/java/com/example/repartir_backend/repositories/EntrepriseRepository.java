package com.example.repartir_backend.repositories;
import com.example.repartir_backend.entities.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntrepriseRepository extends JpaRepository<Entreprise, Integer> {
    Optional<Entreprise> findByUtilisateurEmail(String email);

    void deleteByUtilisateurId(int id);
}
