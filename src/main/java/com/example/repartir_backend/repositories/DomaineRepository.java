package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Domaine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DomaineRepository extends JpaRepository<Domaine, Integer> {
}
