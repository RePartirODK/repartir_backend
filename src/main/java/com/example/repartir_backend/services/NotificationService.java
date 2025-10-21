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
            // On récupère le véritable utilisateur correspondant à l’admin
            Utilisateur utilisateur = utilisateurRepository.findById(admin.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur admin non trouvé avec l'id : " + admin.getId()));
            creerEtEnvoyerNotification(utilisateur, message);
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
        creerEtEnvoyerNotification(destinataire, message);
    }

    /**
     * Méthode privée factorisée pour créer et envoyer une notification.
     * @param destinataire L'utilisateur à notifier.
     * @param message Le message à envoyer.
     */
    private void creerEtEnvoyerNotification(Utilisateur destinataire, String message) {
        Notification notification = new Notification();
        notification.setDestinataire(destinataire);
        notification.setMessage(message);
        notification.setDateCreation(LocalDateTime.now());
        notification.setLue(false);

        // Sauvegarde en base
        notificationRepository.save(notification);

        // Envoi en temps réel via WebSocket
        try {
            messagingTemplate.convertAndSendToUser(
                    destinataire.getEmail(),
                    "/queue/notifications",
                    NotificationDto.fromEntity(notification) // envoyer un DTO plus léger que l’entité complète
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
     * Récupère les notifications non lues d’un administrateur spécifique.
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsNonLuesAdmin(int adminId) {
        List<Notification> notifications =
                notificationRepository.findByDestinataireAdminIdAndLueIsFalseOrderByDateCreationDesc(adminId);
        return NotificationDto.fromEntities(notifications);
    }

}
