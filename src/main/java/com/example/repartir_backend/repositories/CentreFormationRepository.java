package com.example.repartir_backend.repositories;
import com.example.repartir_backend.entities.CentreFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CentreFormationRepository extends JpaRepository<CentreFormation, Integer> {
    @Query("SELECT c FROM CentreFormation c WHERE c.utilisateur.estActive = true")
    List<CentreFormation> findCentresActifs();

    Optional<CentreFormation> findByUtilisateur(Utilisateur utilisateur);
    Optional<CentreFormation> findByUtilisateur_Id(int id);

    void deleteByUtilisateurId(int id);
}
