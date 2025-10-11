package com.example.repartir_backend.services;

import com.example.repartir_backend.repositories.ParrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParrainageServices {
    private final ParrainRepository parrainRepository;
}
