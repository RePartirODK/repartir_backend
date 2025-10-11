package com.example.repartir_backend.services;

import com.example.repartir_backend.repositories.MentorRepository;
import com.example.repartir_backend.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServices {
    private final MessageRepository messageRepository;
}
