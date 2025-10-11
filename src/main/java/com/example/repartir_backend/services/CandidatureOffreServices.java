package com.example.repartir_backend.services;

import com.example.repartir_backend.repositories.CandidatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CandidatureOffreServices {
    private final CandidatureRepository candidatureRepository;

}
