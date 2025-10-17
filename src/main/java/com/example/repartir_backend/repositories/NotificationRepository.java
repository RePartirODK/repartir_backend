package com.example.repartir_backend.repositories;

import com.example.repartir_backend.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'accès aux données de l'entité {@link Notification}.
 * Fournit les méthodes CRUD de base et des requêtes personnalisées pour gérer les notifications.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    /**
     * Trouve toutes les notifications non lues pour un destinataire spécifique,
     * triées par date de création décroissante pour afficher les plus récentes en premier.
     * @param destinataireId L'ID de l'utilisateur destinataire.
     * @return Une liste de notifications non lues.
     */
    List<Notification> findByDestinataireIdAndLueIsFalseOrderByDateCreationDesc(int destinataireId);
}
