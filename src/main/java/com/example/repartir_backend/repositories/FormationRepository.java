package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Formation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormationRepository extends JpaRepository<Formation, Integer> {
    List<Formation> findAllByCentreFormation_Id(int id);

    List<Formation> findByCentreFormationId(int centreId);
}
