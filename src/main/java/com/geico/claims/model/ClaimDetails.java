package com.geico.claims.model;

public record ClaimDetails(
        String incidentDate,
        String damageType,
        String vehiclePart,
        String severity,
        String summary
) {
}
