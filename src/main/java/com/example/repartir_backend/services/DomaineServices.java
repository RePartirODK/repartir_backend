package com.example.repartir_backend.services;

import com.example.repartir_backend.repositories.DomaineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DomaineServices {
    private final DomaineRepository domaineRepository;
}
