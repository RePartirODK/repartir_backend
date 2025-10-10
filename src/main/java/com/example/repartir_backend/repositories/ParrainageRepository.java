package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Parrainage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParrainageRepository extends JpaRepository<Parrainage, Integer> {
}
