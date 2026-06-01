package com.geico.claims.controller;

import com.geico.claims.agent.AgentResponse;
import com.geico.claims.agent.ClaimsAgentOrchestrator;
import com.geico.claims.agent.ConversationalClaimsAgent;
import com.geico.claims.model.ChatRequest;
import com.geico.claims.model.ChatResponse;
import com.geico.claims.model.ClaimDetails;
import com.geico.claims.model.ClaimRequest;
import com.geico.claims.service.ClaimService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
public class ClaimsController {

    private final ClaimService claimService;
    private final ClaimsAgentOrchestrator agentOrchestrator;
    private final ConversationalClaimsAgent conversationalAgent;

    public ClaimsController(
            ClaimService claimService,
            ClaimsAgentOrchestrator agentOrchestrator,
            ConversationalClaimsAgent conversationalAgent
    ) {
        this.claimService = claimService;
        this.agentOrchestrator = agentOrchestrator;
        this.conversationalAgent = conversationalAgent;
    }

    // Day 1 - Simple extraction
    @PostMapping("/analyze")
    public ClaimDetails analyzeClaim(@RequestBody ClaimRequest request) {
        if (request.description() == null || request.description().isBlank()) {
            throw new IllegalArgumentException("Claim description cannot be empty");
        }
        return claimService.analyzeClaim(request.description());
    }

    // Day 2 - Full agent processing with tools (single-shot)
    @PostMapping("/process")
    public AgentResponse processClaim(@RequestBody ClaimRequest request) {
        if (request.description() == null || request.description().isBlank()) {
            throw new IllegalArgumentException("Claim description cannot be empty");
        }
        return agentOrchestrator.processClaim(request.description());
    }

    // Day 3 - Conversational agent with memory (multi-turn)
    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String reply = conversationalAgent.chat(request.sessionId(), request.message());
        return new ChatResponse(request.sessionId(), reply);
    }

    // Info endpoint - list available policies for testing
    @GetMapping("/policies")
    public List<Map<String, String>> listPolicies() {
        return List.of(
                Map.of("policyId", "POL-1001", "customer", "John Smith", "plan", "Premium", "vehicle", "2022 Toyota Camry"),
                Map.of("policyId", "POL-1002", "customer", "Sarah Johnson", "plan", "Basic", "vehicle", "2020 Honda Civic"),
                Map.of("policyId", "POL-1003", "customer", "Mike Davis", "plan", "Premium", "vehicle", "2023 Ford F-150"),
                Map.of("policyId", "POL-1004", "customer", "Emily Brown", "plan", "Comprehensive (EXPIRED)", "vehicle", "2024 Tesla Model 3")
        );
    }

    // Health check
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "agent", "Insurance Claims Agent",
                "model", "llama3.1 (Ollama)",
                "version", "1.0.0"
        );
    }
}
