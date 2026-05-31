package com.geico.claims.agent;

import com.geico.claims.tools.CostEstimatorTool;
import com.geico.claims.tools.CoverageCheckTool;
import com.geico.claims.tools.PolicyLookupTool;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClaimsAgentConfig {

    @Bean
    public ClaimsAgent claimsAgent(
            ChatLanguageModel chatLanguageModel,
            PolicyLookupTool policyLookupTool,
            CoverageCheckTool coverageCheckTool,
            CostEstimatorTool costEstimatorTool
    ) {
        return AiServices.builder(ClaimsAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(policyLookupTool, coverageCheckTool, costEstimatorTool)
                .build();
    }
}
