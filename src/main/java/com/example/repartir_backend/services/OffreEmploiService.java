package com.example.repartir_backend.services;

import com.example.repartir_backend.repositories.OffreEmploiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OffreEmploiService {
    private final OffreEmploiRepository offreEmploiRepository;
}
