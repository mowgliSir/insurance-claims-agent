package com.geico.claims.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CostEstimatorToolTest {

    private CostEstimatorTool tool;

    @BeforeEach
    void setUp() {
        tool = new CostEstimatorTool();
    }

    @Test
    void shouldEstimateCostForSinglePart() {
        String result = tool.estimateCost("rear bumper", "moderate", 500.0);

        assertTrue(result.contains("Total: $1400.00"));
        assertTrue(result.contains("Customer pays: $500.00"));
        assertTrue(result.contains("Insurance pays: $900.00"));
    }

    @Test
    void shouldApplySeverityMultiplier() {
        String result = tool.estimateCost("rear bumper", "severe", 500.0);

        // base 1400 * 1.8 severity = 2520
        assertTrue(result.contains("Total: $2520.00"));
        assertTrue(result.contains("Customer pays: $500.00"));
        assertTrue(result.contains("Insurance pays: $2020.00"));
    }

    @Test
    void shouldEstimateMultipleParts() {
        String result = tool.estimateCost("hood, windshield", "moderate", 750.0);

        // hood 2000 + windshield 800 = 2800
        assertTrue(result.contains("Total: $2800.00"));
        assertTrue(result.contains("Customer pays: $750.00"));
        assertTrue(result.contains("Insurance pays: $2050.00"));
    }

    @Test
    void shouldHandleMinorSeverity() {
        String result = tool.estimateCost("mirror", "minor", 300.0);

        // base 300 * 0.5 = 150, customer pays all (150 < 300 deductible)
        assertTrue(result.contains("Total: $150.00"));
        assertTrue(result.contains("Customer pays: $150.00"));
        assertTrue(result.contains("Insurance pays: $0.00"));
    }

    @Test
    void shouldUseDefaultForUnknownParts() {
        String result = tool.estimateCost("engine", "moderate", 500.0);

        // unknown part defaults to $1000
        assertTrue(result.contains("Total: $1000.00"));
    }
}
