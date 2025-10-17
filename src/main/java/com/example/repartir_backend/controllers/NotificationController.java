package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.NotificationDto;
import com.example.repartir_backend.entities.Notification;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.repositories.NotificationRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import com.example.repartir_backend.services.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des notifications d'un utilisateur.
 * Expose des endpoints pour récupérer les notifications non lues et pour les marquer comme lues.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;

    /**
     * Récupère la liste de toutes les notifications non lues pour l'utilisateur authentifié.
     * @param principal L'objet Principal injecté par Spring Security, représentant l'utilisateur authentifié.
     * @return Une liste de notifications, qui peut être vide si il n'y en a aucune.
     */
    @GetMapping("/non-lues")
    public ResponseEntity<List<NotificationDto>> getNotificationsNonLues(Principal principal) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));

        List<NotificationDto> notifications = notificationService.getNotificationsNonLues(utilisateur.getId());

        return ResponseEntity.ok(notifications);
    }

    /**
     * Marque une notification spécifique comme "lue".
     * L'utilisateur authentifié ne peut marquer que ses propres notifications.
     * @param id L'ID de la notification à marquer comme lue.
     * @param principal L'utilisateur authentifié.
     * @return Une réponse HTTP 200 OK si l'opération réussit.
     * @throws EntityNotFoundException si la notification ou l'utilisateur n'est pas trouvé.
     * @throws AccessDeniedException si l'utilisateur essaie de marquer une notification qui ne lui appartient pas.
     */
    @PostMapping("/{id}/marquer-comme-lue")
    public ResponseEntity<Void> marquerCommeLue(@PathVariable int id, Principal principal) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé."));

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification non trouvée."));

        // Vérification de sécurité : s'assurer que l'utilisateur ne modifie que ses propres notifications.
        if (notification.getDestinataire().getId() != utilisateur.getId()) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier cette notification.");
        }

        notification.setLue(true);
        notificationRepository.save(notification);

        return ResponseEntity.ok().build();
    }
}
