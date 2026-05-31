package com.geico.claims.service;

import com.geico.claims.model.ClaimDetails;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ClaimExtractor {

    @SystemMessage("""
            You are an insurance claims analyst at GEICO.
            Extract structured information from the customer's claim description.
            Always respond in valid JSON format with these fields:
            - incidentDate: the date of the incident (use "unknown" if not provided)
            - damageType: type of damage (e.g., collision, theft, vandalism, weather)
            - vehiclePart: which part of the vehicle is damaged (e.g., front bumper, windshield, rear door)
            - severity: estimated severity (minor, moderate, severe)
            - summary: one-line summary of the claim
            Respond ONLY with the JSON object, no extra text.
            """)
    @UserMessage("Extract claim details from: {{it}}")
    ClaimDetails extractDetails(String claimDescription);
}
