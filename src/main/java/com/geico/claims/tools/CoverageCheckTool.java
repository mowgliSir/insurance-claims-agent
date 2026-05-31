package com.geico.claims.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CoverageCheckTool {

    // Maps damage types to required coverage
    private static final Map<String, String> DAMAGE_TO_COVERAGE = Map.of(
            "collision", "collision",
            "theft", "theft",
            "vandalism", "comprehensive",
            "weather", "weather",
            "fire", "comprehensive",
            "flood", "comprehensive",
            "hail", "weather",
            "hit and run", "collision"
    );

    @Tool("Check if a specific damage type is covered under a policy's coverage list. Provide the damage type and the list of coverages from the policy.")
    public String checkCoverage(
            @P("The type of damage, e.g., collision, theft, vandalism, weather") String damageType,
            @P("Comma-separated list of coverage types from the policy") String policyCoverages
    ) {
        String normalizedDamage = damageType.toLowerCase().trim();
        List<String> coverages = List.of(policyCoverages.toLowerCase().split(",\\s*"));

        String requiredCoverage = DAMAGE_TO_COVERAGE.getOrDefault(normalizedDamage, "comprehensive");

        boolean isCovered = coverages.contains(requiredCoverage) || coverages.contains("comprehensive");

        if (isCovered) {
            return String.format(
                    "COVERED: Damage type '%s' requires '%s' coverage, which IS included in the policy coverages [%s].",
                    normalizedDamage, requiredCoverage, String.join(", ", coverages)
            );
        } else {
            return String.format(
                    "NOT COVERED: Damage type '%s' requires '%s' coverage, which is NOT included in the policy coverages [%s].",
                    normalizedDamage, requiredCoverage, String.join(", ", coverages)
            );
        }
    }
}
