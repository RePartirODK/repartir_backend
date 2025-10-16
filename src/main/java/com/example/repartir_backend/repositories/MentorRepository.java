package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRepository extends JpaRepository<Mentor, Integer> {
    void deleteByUtilisateurId(int id);
}
