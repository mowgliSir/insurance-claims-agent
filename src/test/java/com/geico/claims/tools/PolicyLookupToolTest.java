package com.geico.claims.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PolicyLookupToolTest {

    private PolicyLookupTool tool;

    @BeforeEach
    void setUp() {
        tool = new PolicyLookupTool();
    }

    @Test
    void shouldReturnPolicyForValidId() {
        String result = tool.lookupPolicy("POL-1001");

        assertTrue(result.contains("John Smith"));
        assertTrue(result.contains("Toyota"));
        assertTrue(result.contains("Premium"));
        assertTrue(result.contains("Active: YES"));
    }

    @Test
    void shouldDetectExpiredPolicy() {
        String result = tool.lookupPolicy("POL-1004");

        assertTrue(result.contains("Emily Brown"));
        assertTrue(result.contains("EXPIRED"));
    }

    @Test
    void shouldReturnNotFoundForInvalidId() {
        String result = tool.lookupPolicy("POL-9999");

        assertTrue(result.contains("not found"));
    }

    @Test
    void shouldListAllPolicies() {
        String result = tool.listPolicies();

        assertTrue(result.contains("POL-1001"));
        assertTrue(result.contains("POL-1002"));
        assertTrue(result.contains("POL-1003"));
        assertTrue(result.contains("POL-1004"));
    }
}
