package com.geico.claims.agent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AgentResponse {

    private String decision;
    private String policyId;
    private String damageType;
    private double estimatedCost;
    private double customerPays;
    private double insurancePays;
    private String reasoning;
    private final List<Map<String, String>> steps = new ArrayList<>();

    public void addStep(String stepName, String result) {
        Map<String, String> step = new LinkedHashMap<>();
        step.put("step", stepName);
        step.put("result", result);
        steps.add(step);
    }

    // Getters and setters
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getPolicyId() { return policyId; }
    public void setPolicyId(String policyId) { this.policyId = policyId; }

    public String getDamageType() { return damageType; }
    public void setDamageType(String damageType) { this.damageType = damageType; }

    public double getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(double estimatedCost) { this.estimatedCost = estimatedCost; }

    public double getCustomerPays() { return customerPays; }
    public void setCustomerPays(double customerPays) { this.customerPays = customerPays; }

    public double getInsurancePays() { return insurancePays; }
    public void setInsurancePays(double insurancePays) { this.insurancePays = insurancePays; }

    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }

    public List<Map<String, String>> getSteps() { return steps; }
}
