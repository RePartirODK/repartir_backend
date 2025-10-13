package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Admin;
import com.example.repartir_backend.entities.RefreshToken;
import com.example.repartir_backend.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUtilisateur_Id(int id_utilisateur);
    void deleteByAdmin_Id(int id_admin);
}
