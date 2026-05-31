package com.geico.claims.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CostEstimatorTool {

    // Average repair costs by vehicle part
    private static final Map<String, Double> PART_COSTS = Map.ofEntries(
            Map.entry("front bumper", 1500.0),
            Map.entry("rear bumper", 1400.0),
            Map.entry("windshield", 800.0),
            Map.entry("rear window", 600.0),
            Map.entry("driver side window", 400.0),
            Map.entry("passenger side window", 400.0),
            Map.entry("hood", 2000.0),
            Map.entry("trunk", 1800.0),
            Map.entry("door", 1200.0),
            Map.entry("rear door", 1200.0),
            Map.entry("front door", 1200.0),
            Map.entry("fender", 900.0),
            Map.entry("roof", 3000.0),
            Map.entry("frame", 5000.0),
            Map.entry("dashboard", 1500.0),
            Map.entry("headlight", 500.0),
            Map.entry("taillight", 350.0),
            Map.entry("mirror", 300.0),
            Map.entry("tire", 250.0)
    );

    // Severity multipliers
    private static final Map<String, Double> SEVERITY_MULTIPLIER = Map.of(
            "minor", 0.5,
            "moderate", 1.0,
            "severe", 1.8
    );

    @Tool("Estimate the repair cost for vehicle damage. Provide the damaged parts (comma-separated), severity level, and the policy deductible amount.")
    public String estimateCost(
            @P("Comma-separated list of damaged vehicle parts") String damagedParts,
            @P("Severity: minor, moderate, or severe") String severity,
            @P("The policy deductible amount in dollars") double deductible
    ) {
        String[] parts = damagedParts.toLowerCase().split(",\\s*");
        double severityMultiplier = SEVERITY_MULTIPLIER.getOrDefault(severity.toLowerCase().trim(), 1.0);

        double totalCost = 0;
        StringBuilder breakdown = new StringBuilder();

        for (String part : parts) {
            String trimmed = part.trim();
            double baseCost = PART_COSTS.getOrDefault(trimmed, 1000.0); // default $1000 for unknown parts
            double adjustedCost = baseCost * severityMultiplier;
            totalCost += adjustedCost;
            breakdown.append(String.format("  - %s: $%.2f (base $%.2f x %.1f severity)\n", trimmed, adjustedCost, baseCost, severityMultiplier));
        }

        double customerPays = Math.min(deductible, totalCost);
        double insurancePays = Math.max(0, totalCost - deductible);

        return String.format(
                "COST ESTIMATE:\n%sTotal: $%.2f | Deductible: $%.2f | Customer pays: $%.2f | Insurance pays: $%.2f",
                breakdown, totalCost, deductible, customerPays, insurancePays
        );
    }
}
