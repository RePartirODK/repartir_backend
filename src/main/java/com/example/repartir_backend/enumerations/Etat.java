package com.example.repartir_backend.enumerations;

/**
 * Représente l'état de validation d'un compte utilisateur.
 */
public enum Etat {
    EN_ATTENTE, // Le compte attend la validation d'un administrateur.
    VALIDE,     // Le compte a été validé et est actif.
    REFUSE,   // Le compte a été refusé par un administrateur.
    SUPPRIME,//le compte est supprimer par l'utilisateur
    EN_COURS,
    TERMINE,
}
