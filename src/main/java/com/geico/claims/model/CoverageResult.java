package com.geico.claims.model;

public record CoverageResult(
        boolean isCovered,
        String coverageType,
        double deductible,
        double maxPayout,
        String reason
) {
}
