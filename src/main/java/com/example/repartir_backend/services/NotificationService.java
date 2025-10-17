package com.example.repartir_backend.services;

import com.example.repartir_backend.entities.Notification;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.enumerations.Role;
import com.example.repartir_backend.repositories.NotificationRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import com.example.repartir_backend.dto.NotificationDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service central pour la gestion des notifications.
 * Il gère la création, la persistance et l'envoi en temps réel des notifications
 * via WebSocket.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;
    /**
     * Template pour l'envoi de messages via WebSocket.
     * C'est le composant clé qui permet de "pousser" les notifications
     * en temps réel vers les clients connectés.
     */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Crée et envoie une notification à l'administrateur du système.
     * Cette méthode recherche l'unique utilisateur avec le rôle ADMIN.
     * @param message Le contenu de la notification.
     * @throws EntityNotFoundException si aucun utilisateur ADMIN n'est trouvé.
     */
    @Transactional
    public void notifierAdmin(String message) {
        Utilisateur admin = utilisateurRepository.findByRole(Role.ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("Aucun administrateur trouvé dans le système."));

        creerEtEnvoyerNotification(admin, message);
    }

    /**
     * Crée et envoie une notification à un utilisateur spécifique.
     * @param destinataire L'objet Utilisateur qui doit recevoir la notification.
     * @param message Le contenu de la notification.
     */
    @Transactional
    public void notifierUtilisateur(Utilisateur destinataire, String message) {
        creerEtEnvoyerNotification(destinataire, message);
    }

    /**
     * Méthode privée qui factorise la logique de création et d'envoi.
     * 1. Crée une nouvelle entité Notification et la sauvegarde en base de données.
     * 2. Envoie la notification via WebSocket à un canal privé de l'utilisateur.
     * Le canal '/user/queue/notifications' est spécifique à chaque utilisateur.
     * Le framework s'occupe de router le message vers le bon utilisateur grâce à son email (Principal).
     * @param destinataire L'utilisateur à notifier.
     * @param message Le message à envoyer.
     */
    private void creerEtEnvoyerNotification(Utilisateur destinataire, String message) {
        Notification notification = new Notification();
        notification.setDestinataire(destinataire);
        notification.setMessage(message);
        notificationRepository.save(notification);

        // Envoyer la notification en temps réel via WebSocket à un topic utilisateur spécifique
        messagingTemplate.convertAndSendToUser(
                destinataire.getEmail(),
                "/queue/notifications",
                notification
        );
    }

    /**
     * Récupère toutes les notifications non lues pour un utilisateur donné.
     * L'annotation @Transactional garantit que la session de base de données reste ouverte,
     * ce qui permet de charger les données associées (comme le destinataire) même si elles
     * sont en chargement paresseux (LAZY), évitant ainsi une LazyInitializationException.
     * @param utilisateurId L'ID de l'utilisateur pour lequel récupérer les notifications.
     * @return Une liste de DTOs de notification, sûre à sérialiser en JSON.
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsNonLues(int utilisateurId) {
        List<Notification> notifications = notificationRepository.findByDestinataireIdAndLueIsFalseOrderByDateCreationDesc(utilisateurId);
        return NotificationDto.fromEntities(notifications);
    }
}
