package com.example.repartir_backend.repositories;


import com.example.repartir_backend.entities.UserDomaine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDomaineRepository extends JpaRepository<UserDomaine, Integer> {
}
