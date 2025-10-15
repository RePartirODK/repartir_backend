package com.example.repartir_backend.controllers;

import com.example.repartir_backend.services.MailSendServices;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mails")
public class MailSendControllers {
    private final MailSendServices mailSendServices;
}
