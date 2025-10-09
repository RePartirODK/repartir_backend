package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
}
