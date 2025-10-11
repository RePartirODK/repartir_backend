package com.example.repartir_backend.services;

import com.example.repartir_backend.repositories.UserDomaineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDomaineServices {
    private final UserDomaineRepository userDomaineRepository;
}
