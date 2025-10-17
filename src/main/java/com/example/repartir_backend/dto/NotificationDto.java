package com.example.repartir_backend.dto;

import com.example.repartir_backend.entities.Notification;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class NotificationDto {

    private int id;
    private String message;
    private boolean lue;
    private LocalDateTime dateCreation;

    public static NotificationDto fromEntity(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .lue(notification.isLue())
                .dateCreation(notification.getDateCreation())
                .build();
    }

    public static List<NotificationDto> fromEntities(List<Notification> notifications) {
        return notifications.stream()
                .map(NotificationDto::fromEntity)
                .collect(Collectors.toList());
    }
}
