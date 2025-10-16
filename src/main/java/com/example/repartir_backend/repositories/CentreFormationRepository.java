package com.example.repartir_backend.repositories;
import com.example.repartir_backend.entities.CentreFormation;
import com.example.repartir_backend.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface CentreFormationRepository extends JpaRepository<CentreFormation, Integer> {
    Optional<CentreFormation> findByUtilisateur_Id(int id);

    void deleteByUtilisateurId(int id);
    @Query("SELECT c FROM CentreFormation c WHERE c.utilisateur.estActive = true")
    List<CentreFormation> findCentresActifs();

    Optional<CentreFormation> findByUtilisateur(Utilisateur utilisateur);
}
