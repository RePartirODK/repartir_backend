package com.example.repartir_backend.repositories;


import com.example.repartir_backend.entities.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement,Integer> {
    List<Paiement> findAllByParrainage_Id(int id);
}
