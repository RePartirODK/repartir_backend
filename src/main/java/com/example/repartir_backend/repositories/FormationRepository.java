package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Formation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormationRepository extends JpaRepository<Formation, Integer> {
}
