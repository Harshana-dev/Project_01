package com.harshana.gemstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // Main method
    public boolean send(String to, String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            return true;
        } catch (Exception e) {
            System.out.println("EMAIL FAILED: " + e.getMessage());
            return false;
        }
    }

    // ✅ Compatibility method (if your controllers call sendSafe)
    public boolean sendSafe(String to, String subject, String body) {
        return send(to, subject, body);
    }
}