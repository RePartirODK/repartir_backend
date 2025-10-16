package com.example.repartir_backend.repositories;


import com.example.repartir_backend.entities.InscriptionFormation;
import com.example.repartir_backend.entities.Paiement;
import com.example.repartir_backend.enumerations.Etat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement,Integer> {
    List<Paiement> findAllByParrainage_Id(int id);
    List<Paiement> findByInscriptionFormationAndStatut(InscriptionFormation inscription, Etat etat);

    List<Paiement> findByInscriptionFormation(int inscription);

    List<Paiement> findByJeuneId(int idJeune);
}
