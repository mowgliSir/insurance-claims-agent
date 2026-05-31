package com.geico.claims.service;

import com.geico.claims.model.ClaimDetails;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.stereotype.Service;

@Service
public class ClaimService {

    private final ClaimExtractor claimExtractor;

    public ClaimService(ChatLanguageModel chatLanguageModel) {
        this.claimExtractor = AiServices.create(ClaimExtractor.class, chatLanguageModel);
    }

    public ClaimDetails analyzeClaim(String description) {
        return claimExtractor.extractDetails(description);
    }
}
