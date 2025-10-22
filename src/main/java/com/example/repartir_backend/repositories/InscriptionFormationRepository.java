package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Formation;
import com.example.repartir_backend.entities.InscriptionFormation;
import com.example.repartir_backend.entities.Jeune;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscriptionFormationRepository extends JpaRepository<InscriptionFormation, Integer> {
    boolean existsByJeuneAndFormation(Jeune jeune, Formation formation);

}
