package com.geico.claims.model;

import java.time.LocalDate;
import java.util.List;

public record Policy(
        String policyId,
        String customerName,
        String vehicleMake,
        String vehicleModel,
        int vehicleYear,
        String planType,
        List<String> coverageTypes,
        double deductible,
        double maxCoverage,
        LocalDate expiryDate
) {
    public boolean isActive() {
        return expiryDate.isAfter(LocalDate.now());
    }
}
