package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.OffreEmploi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OffreEmploiRepository extends JpaRepository<OffreEmploi, Integer> {
    List<OffreEmploi> findByEntrepriseId(int entrepriseId);
    
    @Query("SELECT o FROM OffreEmploi o JOIN FETCH o.entreprise e JOIN FETCH e.utilisateur")
    List<OffreEmploi> findAllWithEntreprise();
    
    @Query("SELECT o FROM OffreEmploi o JOIN FETCH o.entreprise e JOIN FETCH e.utilisateur WHERE o.id = :id")
    java.util.Optional<OffreEmploi> findByIdWithEntreprise(int id);
}
