package com.geico.claims.model;

public record ChatRequest(String sessionId, String message) {

    public ChatRequest {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = java.util.UUID.randomUUID().toString();
        }
    }
}
