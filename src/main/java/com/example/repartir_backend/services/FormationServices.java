package com.example.repartir_backend.services;

import com.example.repartir_backend.repositories.FormationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FormationServices {
    private final FormationRepository formationRepository;
}
