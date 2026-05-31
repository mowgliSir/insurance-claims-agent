package com.geico.claims.model;

public record CostEstimate(
        double estimatedCost,
        double customerPays,
        double insurancePays,
        String breakdown
) {
}
