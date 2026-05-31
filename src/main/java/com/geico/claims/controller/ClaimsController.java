package com.geico.claims.controller;

import com.geico.claims.model.ClaimDetails;
import com.geico.claims.model.ClaimRequest;
import com.geico.claims.service.ClaimService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/claims")
public class ClaimsController {

    private final ClaimService claimService;

    public ClaimsController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping("/analyze")
    public ClaimDetails analyzeClaim(@RequestBody ClaimRequest request) {
        return claimService.analyzeClaim(request.description());
    }
}
