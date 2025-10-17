package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.enumerations.Etat;
import com.example.repartir_backend.enumerations.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Dépôt pour l'accès aux données de l'entité Utilisateur.
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    /**
     * Recherche un utilisateur par son adresse e-mail.
     * @param email L'email de l'utilisateur.
     * @return Un Optional contenant l'utilisateur s'il est trouvé.
     */
    Optional<Utilisateur> findByEmail(String email);
    List<Utilisateur> findByEtat(Etat etat);

    /**
     * Trouve un utilisateur par son rôle.
     * Utile pour trouver des utilisateurs uniques comme l'administrateur.
     * @param role Le rôle à rechercher.
     * @return Un Optional contenant l'utilisateur s'il est trouvé.
     */
    Optional<Utilisateur> findByRole(Role role);
}
