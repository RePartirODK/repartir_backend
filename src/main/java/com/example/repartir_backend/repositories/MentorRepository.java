package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MentorRepository extends JpaRepository<Mentor, Integer> {
    void deleteByUtilisateurId(int id);
    Optional<Mentor> findByUtilisateur_Email(String email);
}
