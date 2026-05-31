package com.geico.claims.agent;

import com.geico.claims.tools.CostEstimatorTool;
import com.geico.claims.tools.CoverageCheckTool;
import com.geico.claims.tools.PolicyLookupTool;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Orchestrates the claims processing pipeline.
 * This is an "agentic" pattern where the AI reasons at each step
 * and decides how to proceed based on tool results.
 *
 * Flow: Extract Details → Lookup Policy → Check Coverage → Estimate Cost → Decision
 */
@Service
public class ClaimsAgentOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(ClaimsAgentOrchestrator.class);

    private final ChatLanguageModel model;
    private final PolicyLookupTool policyLookupTool;
    private final CoverageCheckTool coverageCheckTool;
    private final CostEstimatorTool costEstimatorTool;

    public ClaimsAgentOrchestrator(
            ChatLanguageModel model,
            PolicyLookupTool policyLookupTool,
            CoverageCheckTool coverageCheckTool,
            CostEstimatorTool costEstimatorTool
    ) {
        this.model = model;
        this.policyLookupTool = policyLookupTool;
        this.coverageCheckTool = coverageCheckTool;
        this.costEstimatorTool = costEstimatorTool;
    }

    public AgentResponse processClaim(String claimDescription) {
        AgentResponse response = new AgentResponse();

        // Step 1: Extract claim details using LLM
        log.info("Step 1: Extracting claim details...");
        String extractionPrompt = String.format("""
                Extract the following from this insurance claim. Respond ONLY in this exact format, one field per line:
                POLICY_ID: <the policy id>
                DAMAGE_TYPE: <one of: collision, theft, vandalism, weather, fire, flood>
                DAMAGED_PARTS: <comma separated list of damaged parts>
                SEVERITY: <one of: minor, moderate, severe>

                Claim: %s
                """, claimDescription);

        String extracted = model.generate(extractionPrompt);
        response.addStep("EXTRACT", extracted);
        log.info("Extracted: {}", extracted);

        // Parse extracted fields
        String policyId = extractField(extracted, "POLICY_ID");
        String damageType = extractField(extracted, "DAMAGE_TYPE");
        String damagedParts = extractField(extracted, "DAMAGED_PARTS");
        String severity = extractField(extracted, "SEVERITY");

        // Step 2: Look up policy
        log.info("Step 2: Looking up policy {}...", policyId);
        String policyInfo = policyLookupTool.lookupPolicy(policyId);
        response.addStep("POLICY_LOOKUP", policyInfo);
        log.info("Policy: {}", policyInfo);

        if (policyInfo.contains("not found")) {
            response.setDecision("DENIED");
            response.setReasoning("Policy " + policyId + " not found in the system.");
            return response;
        }

        // Check if policy is active
        if (policyInfo.contains("EXPIRED")) {
            response.setDecision("DENIED");
            response.setReasoning("Policy " + policyId + " is expired. Customer needs to renew their policy.");
            return response;
        }

        // Extract coverages and deductible from policy info
        String coverages = extractBetween(policyInfo, "Coverages: ", " |");
        String deductibleStr = extractBetween(policyInfo, "Deductible: $", " |");
        String maxCoverageStr = extractBetween(policyInfo, "Max Coverage: $", " |");
        double deductible = parseDouble(deductibleStr, 500.0);
        double maxCoverage = parseDouble(maxCoverageStr, 50000.0);

        // Step 3: Check coverage
        log.info("Step 3: Checking coverage for {} against {}...", damageType, coverages);
        String coverageResult = coverageCheckTool.checkCoverage(damageType, coverages);
        response.addStep("COVERAGE_CHECK", coverageResult);
        log.info("Coverage: {}", coverageResult);

        if (coverageResult.contains("NOT COVERED")) {
            response.setDecision("DENIED");
            response.setReasoning("Damage type '" + damageType + "' is not covered under policy " + policyId + ".");
            return response;
        }

        // Step 4: Estimate cost
        log.info("Step 4: Estimating repair cost...");
        String costResult = costEstimatorTool.estimateCost(damagedParts, severity, deductible);
        response.addStep("COST_ESTIMATE", costResult);
        log.info("Cost: {}", costResult);

        // Parse costs
        double totalCost = extractCost(costResult, "Total: $");
        double customerPays = extractCost(costResult, "Customer pays: $");
        double insurancePays = extractCost(costResult, "Insurance pays: $");

        // Step 5: Make decision using LLM
        log.info("Step 5: Making final decision...");
        String decisionPrompt = String.format("""
                You are an insurance claims adjuster. Based on the following information, make a decision.

                Policy Info: %s
                Coverage Check: %s
                Cost Estimate: %s
                Max Coverage: $%.2f

                Rules:
                - APPROVED: If covered and total cost is within max coverage
                - ESCALATE: If total cost exceeds 80%% of max coverage ($%.2f), or involves bodily injury
                - DENIED: If not covered or policy expired

                Respond in this exact format:
                DECISION: <APPROVED or ESCALATE or DENIED>
                REASONING: <one sentence explanation>
                """, policyInfo, coverageResult, costResult, maxCoverage, maxCoverage * 0.8);

        String decisionResult = model.generate(decisionPrompt);
        log.info("Decision: {}", decisionResult);

        String decision = extractField(decisionResult, "DECISION");
        String reasoning = extractField(decisionResult, "REASONING");

        response.setDecision(decision);
        response.setReasoning(reasoning);
        response.setPolicyId(policyId);
        response.setDamageType(damageType);
        response.setEstimatedCost(totalCost);
        response.setCustomerPays(customerPays);
        response.setInsurancePays(insurancePays);

        return response;
    }

    private String extractField(String text, String field) {
        for (String line : text.split("\n")) {
            if (line.trim().toUpperCase().startsWith(field.toUpperCase() + ":")) {
                return line.substring(line.indexOf(":") + 1).trim();
            }
        }
        return "unknown";
    }

    private String extractBetween(String text, String start, String end) {
        int startIdx = text.indexOf(start);
        if (startIdx == -1) return "";
        startIdx += start.length();
        int endIdx = text.indexOf(end, startIdx);
        if (endIdx == -1) return text.substring(startIdx).trim();
        return text.substring(startIdx, endIdx).trim();
    }

    private double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value.replace(",", ""));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private double extractCost(String text, String prefix) {
        int idx = text.indexOf(prefix);
        if (idx == -1) return 0;
        idx += prefix.length();
        StringBuilder sb = new StringBuilder();
        while (idx < text.length() && (Character.isDigit(text.charAt(idx)) || text.charAt(idx) == '.')) {
            sb.append(text.charAt(idx++));
        }
        return parseDouble(sb.toString(), 0);
    }
}
