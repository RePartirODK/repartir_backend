package com.example.repartir_backend.services;

import com.example.repartir_backend.repositories.InscriptionFormationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InscriptionFormationServices {
    private final InscriptionFormationRepository inscriptionFormationRepository;
}
