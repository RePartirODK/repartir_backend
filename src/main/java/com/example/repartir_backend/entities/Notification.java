package com.example.repartir_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Représente une notification destinée à un utilisateur spécifique.
 * Ce système est utilisé pour informer les utilisateurs d'événements importants
 * tels que la validation d'un compte, l'acceptation d'une demande, etc.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    /**
     * Identifiant unique de la notification.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Le contenu textuel de la notification qui sera affiché à l'utilisateur.
     */
    @Column(nullable = false)
    private String message;

    /**
     * L'utilisateur qui doit recevoir cette notification.
     * La liaison est LAZY pour ne pas charger l'utilisateur inutilement
     * lors du chargement d'une liste de notifications.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Utilisateur destinataire;

    /**
     * Indique si la notification a été lue par le destinataire.
     * Par défaut à 'false' lors de la création.
     */
    @Column(nullable = false)
    private boolean lue = false;

    /**
     * La date et l'heure de création de la notification.
     * Automatiquement initialisée au moment de la création.
     */
    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();
}
