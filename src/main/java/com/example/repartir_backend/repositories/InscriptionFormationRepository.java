package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Formation;
import com.example.repartir_backend.entities.InscriptionFormation;
import com.example.repartir_backend.entities.Jeune;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InscriptionFormationRepository extends JpaRepository<InscriptionFormation, Integer> {
    boolean existsByJeuneAndFormation(Jeune jeune, Formation formation);
    List<InscriptionFormation> findByJeune(Jeune jeune);

    // New: list inscriptions for a given formation
    List<InscriptionFormation> findAllByFormation_Id(int formationId);

    // New: list inscriptions for all formations of a centre
    List<InscriptionFormation> findAllByFormation_CentreFormation_Id(int centreId);
    
    // Ajout : trouver une inscription par jeune et formation
    InscriptionFormation findByJeune_IdAndFormation_Id(int jeuneId, int formationId);
}

