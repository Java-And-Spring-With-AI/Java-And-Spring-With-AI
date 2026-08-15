package com.example.supportassistant.domain;

public record SupportAnalysis(String summary, SupportCategory category, Urgency urgency, String suggestedResponse) {
}
