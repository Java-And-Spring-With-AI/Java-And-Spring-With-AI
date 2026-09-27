package org.example.supportassistantspringai.domain;

public record SupportAnalysis(String summary, SupportCategory category, Urgency urgency, String suggestedResponse) {
}
