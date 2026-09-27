package org.example.supportassistantspringai.controller;

import org.example.supportassistantspringai.domain.AnalyzeSupportRequest;
import org.example.supportassistantspringai.domain.SupportAnalysis;
import org.example.supportassistantspringai.domain.SupportCategory;
import org.example.supportassistantspringai.domain.Urgency;
import org.example.supportassistantspringai.service.SupportRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SupportRequestController.class)
class SupportRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SupportRequestService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAnalyze() throws Exception {
        final AnalyzeSupportRequest request = new AnalyzeSupportRequest("My account is locked");
        final SupportAnalysis expectedAnalysis = new SupportAnalysis("Account locked", SupportCategory.ACCOUNT,
                Urgency.HIGH, "We will help you unlock it.");

        when(this.service.analyzeRequest(anyString())).thenReturn(expectedAnalysis);

        this.mockMvc
                .perform(post("/api/support/requests/analyze").contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.summary").value("Account locked"))
                .andExpect(jsonPath("$.category").value("ACCOUNT")).andExpect(jsonPath("$.urgency").value("HIGH"))
                .andExpect(jsonPath("$.suggestedResponse").value("We will help you unlock it."));
    }

    @Test
    void testAnalyzeBadRequest() throws Exception {
        final AnalyzeSupportRequest request = new AnalyzeSupportRequest("");

        when(this.service.analyzeRequest(anyString()))
                .thenThrow(new IllegalArgumentException("The customer message must not be empty."));

        this.mockMvc
                .perform(post("/api/support/requests/analyze").contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The customer message must not be empty."));
    }
}
