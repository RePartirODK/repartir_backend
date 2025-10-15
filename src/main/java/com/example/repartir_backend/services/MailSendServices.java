package com.example.repartir_backend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class MailSendServices {
    private final JavaMailSender javaMailSender;

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
        helper.setText(htmlContent, true);// true est pour activité le code html
        javaMailSender.send(mimeMailMessage);
    }

    public void envoyerEmailBienvenu(String to,String sujet, String nom, String path) throws IOException, MessagingException {
        String template = Files.readString(Paths.get(path));
        template = template.replace("{{nom}}", nom);
        envoiMimeMessage(to,sujet, template);
    }

    public void envoyerCode(String email,
                            String reinitialisationDuMotDePasse,
                            String nom,
                            String path,
                            String code) throws IOException, MessagingException {
        String template = Files.readString(Paths.get(path));
        template = template.replace("{{nom}}", nom);
        template = template.replace("{{code}}", code);

        envoiMimeMessage(email, reinitialisationDuMotDePasse, template);
    }
}
