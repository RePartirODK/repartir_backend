package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Admin;
import com.example.repartir_backend.enumerations.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Dépôt pour l'accès aux données de l'entité Admin.
 */
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    /**
     * Recherche un administrateur par son adresse e-mail.
     * @param email L'adresse e-mail de l'administrateur à rechercher.
     * @return Un Optional contenant l'administrateur s'il est trouvé, sinon un Optional vide.
     */
    Optional<Admin> findByEmail(String email);

    Admin findByRole(Role role);
}
