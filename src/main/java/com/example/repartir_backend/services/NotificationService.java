package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.NotificationDto;
import com.example.repartir_backend.entities.Admin;
import com.example.repartir_backend.entities.Notification;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.repositories.AdminRepository;
import com.example.repartir_backend.repositories.NotificationRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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
    private final AdminRepository adminRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Crée et envoie une notification à tous les administrateurs du système.
     * @param message Le contenu de la notification.
     */
    @Transactional
    public void notifierAdmin(String message) {
        List<Admin> admins = adminRepository.findAll();

        if (admins.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun administrateur trouvé");
        }

        // On notifie chaque admin
        for (Admin admin : admins) {
            creerEtEnvoyerNotification(null, admin, message);
        }
    }

    /**
     * Crée et envoie une notification à un utilisateur spécifique.
     * @param destinataire L'objet Utilisateur qui doit recevoir la notification.
     * @param message Le contenu de la notification.
     */
    @Transactional
    public void notifierUtilisateur(Utilisateur destinataire, String message) {
        if (destinataire == null) {
            throw new IllegalArgumentException("Le destinataire ne peut pas être null");
        }
        creerEtEnvoyerNotification(destinataire,null, message);
    }

    /**
     * Méthode privée factorisée pour créer et envoyer une notification.
     * @param destinataireUtilisateur L'utilisateur à notifier.
     * @param message Le message à envoyer.
     */
    private void creerEtEnvoyerNotification(Utilisateur destinataireUtilisateur, Admin destinataireAdmin, String message) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setDateCreation(LocalDateTime.now());
        notification.setLue(false);

        // Définir le bon destinataire
        if (destinataireUtilisateur != null && destinataireAdmin != null) {
            throw new IllegalArgumentException("Une notification ne peut pas avoir deux destinataires (Admin et Utilisateur).");
        } else if (destinataireUtilisateur != null) {
            notification.setDestinataire(destinataireUtilisateur);
        } else if (destinataireAdmin != null) {
            notification.setDestinataireAdmin(destinataireAdmin);
        } else {
            throw new IllegalArgumentException("Une notification doit avoir au moins un destinataire (Admin ou Utilisateur).");
        }

        // Sauvegarde en base
        notificationRepository.save(notification);

        // Envoi via WebSocket
        try {
            String destinataireEmail = destinataireUtilisateur != null
                    ? destinataireUtilisateur.getEmail()
                    : destinataireAdmin.getEmail();

            messagingTemplate.convertAndSendToUser(
                    destinataireEmail,
                    "/queue/notifications",
                    NotificationDto.fromEntity(notification) // DTO allégé
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de l’envoi WebSocket : " + e.getMessage());
        }
    }

    /**
     * Récupère toutes les notifications non lues d’un utilisateur.
     * @param utilisateurId L’ID de l’utilisateur.
     * @return Liste de NotificationDto.
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsNonLues(int utilisateurId) {
        if (!utilisateurRepository.existsById(utilisateurId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé avec l'id " + utilisateurId);
        }

        List<Notification> notifications = notificationRepository
                .findByDestinataireIdAndLueIsFalseOrderByDateCreationDesc(utilisateurId);

        return NotificationDto.fromEntities(notifications);
    }
    /**
     * Récupère les notifications non lues d'un administrateur spécifique par son ID.
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsNonLuesAdmin(int adminId) {
        List<Notification> notifications =
                notificationRepository.findByDestinataireAdminIdAndLueIsFalseOrderByDateCreationDesc(adminId);
        return NotificationDto.fromEntities(notifications);
    }

    /**
     * Récupère les notifications non lues d'un administrateur spécifique par son email.
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsNonLuesAdmin(String adminEmail) {
        Admin admin = adminRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin non trouvé avec l'email " + adminEmail));
        return getNotificationsNonLuesAdmin(admin.getId());
    }

    /**
     * Marque une notification comme lue pour un administrateur.
     */
    @Transactional
    public void marquerCommeLueAdmin(Integer notificationId, String adminEmail) {
        Admin admin = adminRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin non trouvé avec l'email " + adminEmail));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification non trouvée avec l'id " + notificationId));

        // Vérification de sécurité : s'assurer que l'admin ne modifie que ses propres notifications
        if (notification.getDestinataireAdmin() == null || notification.getDestinataireAdmin().getId() != admin.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'êtes pas autorisé à modifier cette notification.");
        }

        notification.setLue(true);
        notificationRepository.save(notification);
    }

}
