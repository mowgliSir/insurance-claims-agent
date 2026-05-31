package com.geico.claims.model;

public record ClaimDecision(
        String decision,
        String policyId,
        String customerName,
        String damageType,
        double estimatedCost,
        double customerPays,
        double insurancePays,
        String reasoning
) {
}
