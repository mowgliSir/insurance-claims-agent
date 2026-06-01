package com.geico.claims.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoverageCheckToolTest {

    private CoverageCheckTool tool;

    @BeforeEach
    void setUp() {
        tool = new CoverageCheckTool();
    }

    @Test
    void shouldReturnCoveredForMatchingCoverage() {
        String result = tool.checkCoverage("collision", "collision, comprehensive, liability");

        assertTrue(result.contains("COVERED"));
        assertFalse(result.contains("NOT COVERED"));
    }

    @Test
    void shouldReturnNotCoveredWhenMissing() {
        String result = tool.checkCoverage("theft", "collision, liability");

        assertTrue(result.contains("NOT COVERED"));
    }

    @Test
    void shouldCoverVandalismWithComprehensive() {
        String result = tool.checkCoverage("vandalism", "collision, comprehensive, liability");

        assertTrue(result.contains("COVERED"));
        assertFalse(result.contains("NOT COVERED"));
    }

    @Test
    void shouldHandleCaseInsensitiveInput() {
        String result = tool.checkCoverage("COLLISION", "Collision, Liability");

        assertTrue(result.contains("COVERED"));
    }
}
