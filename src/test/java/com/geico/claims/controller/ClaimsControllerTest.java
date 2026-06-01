package com.geico.claims.controller;

import com.geico.claims.agent.AgentResponse;
import com.geico.claims.agent.ClaimsAgentOrchestrator;
import com.geico.claims.agent.ConversationalClaimsAgent;
import com.geico.claims.model.ClaimDetails;
import com.geico.claims.service.ClaimService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClaimsController.class)
class ClaimsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClaimService claimService;

    @MockBean
    private ClaimsAgentOrchestrator agentOrchestrator;

    @MockBean
    private ConversationalClaimsAgent conversationalAgent;

    @Test
    void healthEndpointShouldReturnUp() throws Exception {
        mockMvc.perform(get("/api/claims/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.agent").value("Insurance Claims Agent"));
    }

    @Test
    void policiesEndpointShouldReturnAllPolicies() throws Exception {
        mockMvc.perform(get("/api/claims/policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].policyId").value("POL-1001"));
    }

    @Test
    void analyzeShouldReturnClaimDetails() throws Exception {
        when(claimService.analyzeClaim(anyString()))
                .thenReturn(new ClaimDetails("2026-06-01", "collision", "rear bumper", "moderate", "Rear-end collision"));

        mockMvc.perform(post("/api/claims/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"Car accident, rear bumper damaged\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.damageType").value("collision"))
                .andExpect(jsonPath("$.vehiclePart").value("rear bumper"));
    }

    @Test
    void analyzeShouldRejectEmptyDescription() throws Exception {
        mockMvc.perform(post("/api/claims/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Claim description cannot be empty"));
    }

    @Test
    void processShouldReturnAgentResponse() throws Exception {
        AgentResponse response = new AgentResponse();
        response.setDecision("APPROVED");
        response.setPolicyId("POL-1001");
        response.setReasoning("Claim is covered");

        when(agentOrchestrator.processClaim(anyString())).thenReturn(response);

        mockMvc.perform(post("/api/claims/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"Policy POL-1001. Collision. Rear bumper.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decision").value("APPROVED"))
                .andExpect(jsonPath("$.policyId").value("POL-1001"));
    }

    @Test
    void chatShouldReturnReply() throws Exception {
        when(conversationalAgent.chat(anyString(), anyString()))
                .thenReturn("Hello! How can I help you with your claim?");

        mockMvc.perform(post("/api/claims/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\": \"s1\", \"message\": \"Hi\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("s1"))
                .andExpect(jsonPath("$.reply").value("Hello! How can I help you with your claim?"));
    }
}
