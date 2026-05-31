package com.geico.claims.tools;

import com.geico.claims.model.Policy;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class PolicyLookupTool {

    // Mock database of policies
    private static final Map<String, Policy> POLICY_DB = Map.of(
            "POL-1001", new Policy(
                    "POL-1001", "John Smith", "Toyota", "Camry", 2022,
                    "Premium",
                    List.of("collision", "comprehensive", "liability", "theft"),
                    500.0, 50000.0,
                    LocalDate.of(2027, 6, 15)
            ),
            "POL-1002", new Policy(
                    "POL-1002", "Sarah Johnson", "Honda", "Civic", 2020,
                    "Basic",
                    List.of("collision", "liability"),
                    1000.0, 25000.0,
                    LocalDate.of(2026, 12, 1)
            ),
            "POL-1003", new Policy(
                    "POL-1003", "Mike Davis", "Ford", "F-150", 2023,
                    "Premium",
                    List.of("collision", "comprehensive", "liability", "theft", "weather"),
                    750.0, 75000.0,
                    LocalDate.of(2027, 3, 20)
            ),
            "POL-1004", new Policy(
                    "POL-1004", "Emily Brown", "Tesla", "Model 3", 2024,
                    "Comprehensive",
                    List.of("collision", "comprehensive", "liability", "theft", "weather", "vandalism"),
                    300.0, 100000.0,
                    LocalDate.of(2025, 1, 10) // expired policy
            )
    );

    @Tool("Look up an insurance policy by policy ID. Returns policy details including customer name, vehicle info, coverage types, deductible, and max coverage amount.")
    public String lookupPolicy(String policyId) {
        Policy policy = POLICY_DB.get(policyId);
        if (policy == null) {
            return "Policy not found for ID: " + policyId;
        }
        return String.format(
                "Policy: %s | Customer: %s | Vehicle: %d %s %s | Plan: %s | Coverages: %s | Deductible: $%.2f | Max Coverage: $%.2f | Active: %s | Expiry: %s",
                policy.policyId(), policy.customerName(),
                policy.vehicleYear(), policy.vehicleMake(), policy.vehicleModel(),
                policy.planType(), String.join(", ", policy.coverageTypes()),
                policy.deductible(), policy.maxCoverage(),
                policy.isActive() ? "YES" : "NO (EXPIRED)",
                policy.expiryDate()
        );
    }

    @Tool("List all available policy IDs in the system.")
    public String listPolicies() {
        return "Available policies: " + String.join(", ", POLICY_DB.keySet());
    }
}
