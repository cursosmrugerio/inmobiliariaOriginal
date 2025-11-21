package com.inmobiliaria.notificacion.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppService {

    private final RestTemplate restTemplate;

    @Value("${app.notifications.whatsapp.enabled:false}")
    private boolean whatsappEnabled;

    @Value("${app.notifications.whatsapp.api-url:}")
    private String apiUrl;

    @Value("${app.notifications.whatsapp.api-token:}")
    private String apiToken;

    @Value("${app.notifications.whatsapp.phone-number-id:}")
    private String phoneNumberId;

    public boolean sendMessage(String to, String message) {
        if (!whatsappEnabled) {
            log.info("WhatsApp deshabilitado. Simulando envío a: {} - Mensaje: {}", to, message);
            return true;
        }

        try {
            String normalizedPhone = normalizePhoneNumber(to);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiToken);

            Map<String, Object> body = new HashMap<>();
            body.put("messaging_product", "whatsapp");
            body.put("to", normalizedPhone);
            body.put("type", "text");

            Map<String, String> text = new HashMap<>();
            text.put("body", message);
            body.put("text", text);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            String url = apiUrl + "/" + phoneNumberId + "/messages";
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("WhatsApp enviado exitosamente a: {}", normalizedPhone);
                return true;
            } else {
                log.error("Error al enviar WhatsApp. Status: {}", response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.error("Error al enviar WhatsApp a {}: {}", to, e.getMessage());
            return false;
        }
    }

    public boolean sendTemplate(String to, String templateName, Map<String, String> parameters) {
        if (!whatsappEnabled) {
            log.info("WhatsApp deshabilitado. Simulando envío de template {} a: {}", templateName, to);
            return true;
        }

        try {
            String normalizedPhone = normalizePhoneNumber(to);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiToken);

            Map<String, Object> body = new HashMap<>();
            body.put("messaging_product", "whatsapp");
            body.put("to", normalizedPhone);
            body.put("type", "template");

            Map<String, Object> template = new HashMap<>();
            template.put("name", templateName);
            template.put("language", Map.of("code", "es_MX"));
            body.put("template", template);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            String url = apiUrl + "/" + phoneNumberId + "/messages";
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Error al enviar template WhatsApp a {}: {}", to, e.getMessage());
            return false;
        }
    }

    private String normalizePhoneNumber(String phone) {
        String cleaned = phone.replaceAll("[^0-9]", "");
        if (cleaned.length() == 10) {
            return "52" + cleaned;
        }
        return cleaned;
    }
}
