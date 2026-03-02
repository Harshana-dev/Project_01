package com.harshana.gemstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // Added import
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // ✅ Added: Reads the toggle from application.properties
    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    // Updated Main method
    public boolean send(String to, String subject, String body) {
        // ✅ Added: Only send if the feature is enabled in properties
        if (!emailEnabled) {
            System.out.println("Email skipped: app.email.enabled is false");
            return true;
        }

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

    // ✅ Compatibility method updated to use try-catch specifically
    public boolean sendSafe(String to, String subject, String body) {
        try {
            return send(to, subject, body);
        } catch (Exception e) {
            System.out.println("EMAIL FAILED in sendSafe: " + e.getMessage());
            return false;
        }
    }
}