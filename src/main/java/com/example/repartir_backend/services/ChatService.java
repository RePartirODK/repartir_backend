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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final MentoringRepository mentoringRepository;
    private final UtilisateurRepository utilisateurRepository;

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
}
