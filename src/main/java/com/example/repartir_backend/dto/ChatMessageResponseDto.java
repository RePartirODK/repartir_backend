package com.example.repartir_backend.dto;

import com.example.repartir_backend.entities.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponseDto {
    private int messageId;
    private String content;
    private int senderId;
    private String senderName;
    private LocalDateTime timestamp;

    public static ChatMessageResponseDto fromEntity(Message message) {
        return ChatMessageResponseDto.builder()
                .messageId(message.getId())
                .content(message.getContenu())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getNom()) // Assumant que Utilisateur a un champ 'nom'
                .timestamp(message.getDate())
                .build();
    }
}
