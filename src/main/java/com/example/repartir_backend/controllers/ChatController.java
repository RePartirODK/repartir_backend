package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.ChatMessageDto;
import com.example.repartir_backend.dto.ChatMessageResponseDto;
import com.example.repartir_backend.entities.Message;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import com.example.repartir_backend.services.ChatService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final UtilisateurRepository utilisateurRepository;

    @MessageMapping("/chat/{mentoringId}")
    public void processMessage(
            @DestinationVariable int mentoringId,
            @Payload ChatMessageDto chatMessageDto,
            Principal principal) {

        logger.info(">>>> [WS] Message reçu pour le mentoring ID {}: {}", mentoringId, chatMessageDto.getContent());
        logger.info(">>>> [WS] Le message vient de l'utilisateur: {}", principal.getName());

        try {
            Utilisateur sender = utilisateurRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé: " + principal.getName()));
            logger.info(">>>> [WS] Expéditeur identifié dans la BDD: {}", sender.getEmail());

            Message savedMessage = chatService.saveMessage(mentoringId, chatMessageDto.getContent(), sender);
            logger.info(">>>> [WS] Message sauvegardé avec l'ID: {}", savedMessage.getId());

            ChatMessageResponseDto responseDto = ChatMessageResponseDto.fromEntity(savedMessage);

            messagingTemplate.convertAndSend("/topic/chat/" + mentoringId, responseDto);
            logger.info(">>>> [WS] Message envoyé au topic /topic/chat/{}", mentoringId);

        } catch (Exception e) {
            logger.error("!!!! [WS] ERREUR lors du traitement du message pour le mentoring ID {}: {}", mentoringId, e.getMessage(), e);
        }
    }

    /**
     * Endpoint REST pour supprimer un message du chat.
     * Seul l'expéditeur du message peut le supprimer.
     * @param messageId L'ID du message à supprimer.
     * @param principal L'utilisateur authentifié.
     * @return Réponse HTTP indiquant le succès ou l'échec de l'opération.
     */
    @DeleteMapping("/api/messages/{messageId}")
    public ResponseEntity<?> supprimerMessage(@PathVariable int messageId, Principal principal) {
        try {
            Utilisateur utilisateur = utilisateurRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé: " + principal.getName()));

            chatService.supprimerMessage(messageId, utilisateur);
            
            logger.info(">>>> [REST] Message {} supprimé par l'utilisateur {}", messageId, principal.getName());
            return ResponseEntity.ok("Message supprimé avec succès");
            
        } catch (EntityNotFoundException e) {
            logger.warn(">>>> [REST] Tentative de suppression d'un message inexistant {} par {}", messageId, principal.getName());
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            logger.warn(">>>> [REST] Tentative de suppression non autorisée du message {} par {}", messageId, principal.getName());
            return ResponseEntity.status(403).body("Vous ne pouvez supprimer que vos propres messages");
        } catch (Exception e) {
            logger.error("!!!! [REST] ERREUR lors de la suppression du message {}: {}", messageId, e.getMessage(), e);
            return ResponseEntity.status(500).body("Erreur interne du serveur");
        }
    }
}
