package com.example.backnut.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendActivationEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Activation de votre compte Coach");
        message.setText("Bonjour " + username + ",\n\nVotre compte Coach a été activé avec succès. Vous pouvez maintenant vous connecter.\n\nCordialement,\nL'équipe.");
        mailSender.send(message);
    }
}
