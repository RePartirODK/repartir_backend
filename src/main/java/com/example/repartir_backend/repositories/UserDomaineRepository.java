package com.example.repartir_backend.repositories;


import com.example.repartir_backend.entities.UserDomaine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserDomaineRepository extends JpaRepository<UserDomaine, Integer> {
    List<UserDomaine> findByDomaineId(int domaineId);

    List<UserDomaine> findByUtilisateurId(int userId);

    Optional<UserDomaine> findByUtilisateurIdAndDomaineId(int userId, int domaineId);
}
