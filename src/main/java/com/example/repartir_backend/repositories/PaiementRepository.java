package com.example.repartir_backend.repositories;


import com.example.repartir_backend.entities.InscriptionFormation;
import com.example.repartir_backend.entities.Paiement;
import com.example.repartir_backend.enumerations.StatutPaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement,Integer> {
    List<Paiement> findAllByParrainage_Id(int id);
    List<Paiement> findByInscriptionFormationAndStatus(InscriptionFormation inscription, StatutPaiement statut);

    List<Paiement> findByInscriptionFormationId(int inscription);

    List<Paiement> findByJeuneId(int idJeune);

    List<Paiement> findByInscriptionFormation_Formation_Id(int idFormation);
    
    /**
     * Récupère tous les paiements avec parrainage et parrain chargés (pour éviter N+1)
     */
    @Query("SELECT p FROM Paiement p " +
           "LEFT JOIN FETCH p.parrainage parr " +
           "LEFT JOIN FETCH parr.parrain " +
           "ORDER BY p.date DESC")
    List<Paiement> findAllWithParrainage();
}
