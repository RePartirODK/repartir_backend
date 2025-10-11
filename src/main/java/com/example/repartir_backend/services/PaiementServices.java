package com.example.repartir_backend.services;

import com.example.repartir_backend.repositories.PaiementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaiementServices {
    private final PaiementRepository paiementRepository;
}
