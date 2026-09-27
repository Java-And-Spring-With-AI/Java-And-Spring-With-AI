package org.example.supportassistantspringai.controller;

import org.example.supportassistantspringai.domain.AnalyzeSupportRequest;
import org.example.supportassistantspringai.domain.SupportAnalysis;
import org.example.supportassistantspringai.service.SupportRequestService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/support/requests")
public class SupportRequestController {

    private final SupportRequestService service;

    public SupportRequestController(SupportRequestService service) {
        this.service = service;
    }

    @PostMapping("/analyze")
    public SupportAnalysis analyze(@RequestBody AnalyzeSupportRequest request) {
        return this.service.analyzeRequest(request.message());
    }
}
