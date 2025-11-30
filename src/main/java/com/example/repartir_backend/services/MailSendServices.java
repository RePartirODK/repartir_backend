package com.example.repartir_backend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class MailSendServices {

    private final JavaMailSender javaMailSender;

    // --- Charge un template depuis le classpath (compatible JAR)
    private String loadTemplateFromClasspath(String classpathPath) throws IOException {
        Resource resource = new ClassPathResource(classpathPath);
        if (!resource.exists()) {
            throw new IOException("Template introuvable dans le classpath: " + classpathPath);
        }
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    public void envoieSimpleMail(String to, String sujet, String contenu){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(sujet);
        message.setSentDate(new Date());
        message.setText(contenu);
        javaMailSender.send(message);
    }

    public void envoiMimeMessage(String to, String sujet, String htmlContent) throws MessagingException {
        MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMailMessage, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(sujet);
        helper.setText(htmlContent, true);// true est pour activer le code html
        javaMailSender.send(mimeMailMessage);
    }

    public void envoyerEmailBienvenu(String to,String sujet, String nom, String path) throws IOException, MessagingException {
        String template = loadTemplateFromClasspath(path);
        template = template.replace("{{nom}}", nom);
        envoiMimeMessage(to,sujet, template);
    }

    public void envoyerCode(String email, String sujet, String nom, String path, String code) throws IOException, MessagingException {
        String template = loadTemplateFromClasspath(path);
        template = template.replace("{{nom}}", nom)
                           .replace("{{code}}", code);
        envoiMimeMessage(email, sujet, template);
    }

    public void acceptionInscription(String to,String sujet
            ,String nom, String formation
            , String path) throws IOException, MessagingException {
        String template = loadTemplateFromClasspath(path);
        template = template.replace("{{nom}}", nom)
                .replace("{{formation}}", formation);
        envoiMimeMessage(to,sujet, template);
    }
    public void inscriptionAdmin(String to,String sujet
            ,String email
            , String path) throws IOException, MessagingException {
        String template = loadTemplateFromClasspath(path);
        template = template.replace("{{email}}", email);
        envoiMimeMessage(to,sujet, template);
    }
}