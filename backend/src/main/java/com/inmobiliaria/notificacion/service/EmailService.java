package com.inmobiliaria.notificacion.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@inmobiliaria.com}")
    private String fromEmail;

    @Value("${app.notifications.email.enabled:false}")
    private boolean emailEnabled;

    public boolean sendEmail(String to, String subject, String body) {
        if (!emailEnabled) {
            log.info("Email deshabilitado. Simulando envío a: {} - Asunto: {}", to, subject);
            return true;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email enviado exitosamente a: {}", to);
            return true;
        } catch (Exception e) {
            log.error("Error al enviar email a {}: {}", to, e.getMessage());
            return false;
        }
    }

    public boolean sendHtmlEmail(String to, String subject, String htmlBody) {
        if (!emailEnabled) {
            log.info("Email deshabilitado. Simulando envío HTML a: {} - Asunto: {}", to, subject);
            return true;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            log.info("Email HTML enviado exitosamente a: {}", to);
            return true;
        } catch (MessagingException e) {
            log.error("Error al enviar email HTML a {}: {}", to, e.getMessage());
            return false;
        }
    }
}
