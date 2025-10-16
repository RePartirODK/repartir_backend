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
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

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
}
