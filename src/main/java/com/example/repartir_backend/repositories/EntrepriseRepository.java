package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntrepriseRepository extends JpaRepository<Entreprise, Integer> {
}
