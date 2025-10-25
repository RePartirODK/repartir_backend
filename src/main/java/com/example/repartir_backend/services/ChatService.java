package com.example.repartir_backend.services;

import com.example.repartir_backend.dto.ChatMessageDto;
import com.example.repartir_backend.entities.Message;
import com.example.repartir_backend.entities.Mentoring;
import com.example.repartir_backend.entities.Utilisateur;
import com.example.repartir_backend.repositories.MessageRepository;
import com.example.repartir_backend.repositories.MentoringRepository;
import com.example.repartir_backend.repositories.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final MentoringRepository mentoringRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Message saveMessage(int mentoringId, String content, Utilisateur sender) {
        Mentoring mentoring = mentoringRepository.findById(mentoringId)
                .orElseThrow(() -> new EntityNotFoundException("Mentoring non trouvé: " + mentoringId));

        // Validation de sécurité : vérifier que l'expéditeur fait bien partie du mentorat
        boolean isParticipant = mentoring.getJeune().getUtilisateur().getId() == sender.getId() ||
                                mentoring.getMentor().getUtilisateur().getId() == sender.getId();

        if (!isParticipant) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à envoyer de message dans cette conversation.");
        }

        Message message = new Message();
        message.setMentoring(mentoring);
        message.setSender(sender);
        message.setContenu(content);
        message.setDate(LocalDateTime.now());

        return messageRepository.save(message);
    }

    /**
     * Supprime un message du chat.
     * Seul l'expéditeur du message peut le supprimer.
     * @param messageId L'ID du message à supprimer.
     * @param currentUser L'utilisateur actuellement connecté.
     * @throws EntityNotFoundException si le message n'existe pas.
     * @throws AccessDeniedException si l'utilisateur n'est pas l'expéditeur du message.
     */
    @Transactional
    public void supprimerMessage(int messageId, Utilisateur currentUser) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message non trouvé avec l'ID : " + messageId));

        // Vérification de sécurité : seul l'expéditeur peut supprimer son message
        if (message.getSender().getId() != currentUser.getId()) {
            throw new AccessDeniedException("Vous ne pouvez supprimer que vos propres messages.");
        }

        // Récupérer l'ID du mentoring avant de supprimer le message
        int mentoringId = message.getMentoring().getId();

        // Supprimer le message
        messageRepository.delete(message);

        // Envoyer une notification WebSocket pour informer les autres participants
        Map<String, Object> suppressionNotification = new HashMap<>();
        suppressionNotification.put("type", "message_deleted");
        suppressionNotification.put("messageId", messageId);
        suppressionNotification.put("deletedBy", currentUser.getNom());
        suppressionNotification.put("timestamp", LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/chat/" + mentoringId, suppressionNotification);
    }
}
