package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
}
