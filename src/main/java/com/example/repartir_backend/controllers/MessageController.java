package com.example.repartir_backend.controllers;

import com.example.repartir_backend.dto.ChatMessageResponseDto;
import com.example.repartir_backend.entities.Message;
import com.example.repartir_backend.entities.Mentoring;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.repositories.MessageRepository;
import com.example.repartir_backend.repositories.MentoringRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mentorings")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "Endpoints pour l'historique des messages")
public class MessageController {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    
    private final MessageRepository messageRepository;
    private final MentoringRepository mentoringRepository;
    private final UtilisateurRepository utilisateurRepository;

    @GetMapping("/{mentoringId}/messages")
    @PreAuthorize("hasAnyRole('MENTOR', 'JEUNE')")
    @Operation(summary = "Récupérer l'historique des messages d'un mentoring")
    public ResponseEntity<List<ChatMessageResponseDto>> getMessageHistory(
            @PathVariable int mentoringId,
            Principal principal) {
        
        try {
            logger.info(">>>> [REST] Récupération historique mentoring {}", mentoringId);
            
            // Vérifier que le mentoring existe
            Mentoring mentoring = mentoringRepository.findById(mentoringId)
                    .orElseThrow(() -> new EntityNotFoundException(
                        "Mentoring non trouvé: " + mentoringId
                    ));
            
            // Récupérer l'utilisateur courant
            Utilisateur currentUser = utilisateurRepository
                .findByEmail(principal.getName())
                .orElseThrow(() -> new EntityNotFoundException(
                    "Utilisateur non trouvé: " + principal.getName()
                ));
            
            // Vérifier que l'utilisateur fait partie du mentoring
            boolean isParticipant = 
                mentoring.getJeune().getUtilisateur().getId() == currentUser.getId() ||
                mentoring.getMentor().getUtilisateur().getId() == currentUser.getId();
            
            if (!isParticipant) {
                logger.warn(">>>> [REST] Accès refusé mentoring {} par {}",
                            mentoringId, principal.getName());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Récupérer tous les messages triés par date ASC
            List<Message> messages = messageRepository
                .findByMentoringIdOrderByDateAsc(mentoringId);
            
            // Convertir en DTOs
            List<ChatMessageResponseDto> response = messages.stream()
                .map(ChatMessageResponseDto::fromEntity)
                .collect(Collectors.toList());
            
            logger.info(">>>> [REST] {} messages retournés", response.size());
            
            return ResponseEntity.ok(response);
            
        } catch (EntityNotFoundException e) {
            logger.error("!!!! [REST] Entité non trouvée: {}", e.getMessage());
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            logger.error("!!!! [REST] Erreur récupération historique: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

