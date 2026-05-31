package com.geico.claims.controller;

import com.geico.claims.agent.AgentResponse;
import com.geico.claims.agent.ClaimsAgentOrchestrator;
import com.geico.claims.model.ClaimDetails;
import com.geico.claims.model.ClaimRequest;
import com.geico.claims.service.ClaimService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/claims")
public class ClaimsController {

    private final ClaimService claimService;
    private final ClaimsAgentOrchestrator agentOrchestrator;

    public ClaimsController(ClaimService claimService, ClaimsAgentOrchestrator agentOrchestrator) {
        this.claimService = claimService;
        this.agentOrchestrator = agentOrchestrator;
    }

    // Day 1 - Simple extraction
    @PostMapping("/analyze")
    public ClaimDetails analyzeClaim(@RequestBody ClaimRequest request) {
        return claimService.analyzeClaim(request.description());
    }

    // Day 2 - Full agent processing with tools
    @PostMapping("/process")
    public AgentResponse processClaim(@RequestBody ClaimRequest request) {
        return agentOrchestrator.processClaim(request.description());
    }
}
