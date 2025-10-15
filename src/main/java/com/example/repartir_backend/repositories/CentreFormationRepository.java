package com.example.repartir_backend.repositories;
import com.example.repartir_backend.entities.CentreFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CentreFormationRepository extends JpaRepository<CentreFormation, Integer> {
    Optional<CentreFormation> findByUtilisateur_Id(int id);

    void deleteByUtilisateurId(int id);
}
