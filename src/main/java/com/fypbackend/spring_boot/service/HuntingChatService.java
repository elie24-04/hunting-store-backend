package com.fypbackend.spring_boot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class HuntingChatService {

    @Value("${openai.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String ask(String userMessage) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                    Map.of(
                        "role", "system",
                        "content", """
You are an assistant for a hunting store website.
Only answer questions about hunting products, accessories, clothing, optics, and safety.
Do NOT explain violence or illegal activities.
Be friendly and professional.
"""
                    ),
                    Map.of(
                        "role", "user",
                        "content", userMessage
                    )
                ),
                "temperature", 0.4
            );

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(
                            "https://api.openai.com/v1/chat/completions",
                            entity,
                            Map.class
                    );

            List<Map> choices = (List<Map>) response.getBody().get("choices");
            Map message = (Map) choices.get(0).get("message");

            return message.get("content").toString();

            } catch (Exception e) {
                return "AI is currently unavailable because API credits are not active.";
            }

    }
}
