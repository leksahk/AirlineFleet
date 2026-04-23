package com.airline.airlineweb.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendErrorAlert(String errorType, String message, String severity) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("rizze7778@gmail.com");
            mailMessage.setTo("rizze7778@gmail.com");

            // Динамічний заголовок залежно від важливості
            String icon = severity.equalsIgnoreCase("CRITICAL") ? "🚨" : "⚠️";
            mailMessage.setSubject(icon + " " + severity + " ERROR: " + errorType);

            String text = String.format(
                    "Системне сповіщення про помилку\n" +
                            "===============================\n" +
                            "Категорія: %s\n" +
                            "Рівень: %s\n" +
                            "Деталі: %s\n" +
                            "===============================\n" +
                            "Час: %s",
                    errorType, severity, message, java.time.LocalDateTime.now()
            );

            mailMessage.setText(text);
            mailSender.send(mailMessage);
        } catch (Exception e) {
            System.err.println("Не вдалося надіслати звіт про помилку: " + e.getMessage());
        }
    }
}