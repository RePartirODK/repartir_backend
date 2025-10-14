package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.enumerations.Etat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Dépôt pour l'accès aux données de l'entité Utilisateur.
 */
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    /**
     * Recherche un utilisateur par son adresse e-mail.
     * @param email L'email de l'utilisateur.
     * @return Un Optional contenant l'utilisateur s'il est trouvé.
     */
    Optional<Utilisateur> findByEmail(String email);
    List<Utilisateur> findByEtat(Etat etat);
}
