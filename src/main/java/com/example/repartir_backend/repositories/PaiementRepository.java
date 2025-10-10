package com.example.repartir_backend.repositories;


import com.example.repartir_backend.entities.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaiementRepository extends JpaRepository<Paiement,Integer> {
}
