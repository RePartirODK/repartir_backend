package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Parrain;
import com.example.repartir_backend.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ParrainRepository extends JpaRepository<Parrain, Integer> {
    Optional<Parrain> findByUtilisateur_Id(int id);

    void deleteByUtilisateurId(int id);

    Optional<Parrain> findByUtilisateur(Utilisateur utilisateur);
}
