package com.example.supportassistant.controller;

import com.example.supportassistant.domain.AnalyzeSupportRequest;
import com.example.supportassistant.domain.SupportAnalysis;
import com.example.supportassistant.exception.SupportAssistantException;
import com.example.supportassistant.service.SupportRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class SupportRequestController implements HttpHandler {

	private final SupportRequestService service;
	private final ObjectMapper objectMapper;

	public SupportRequestController(SupportRequestService service, ObjectMapper objectMapper) {
		this.service = service;
		this.objectMapper = objectMapper;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
				this.sendError(exchange, 405, "Method Not Allowed");
				return;
			}

			if (!"/api/support/requests/analyze".equals(exchange.getRequestURI().getPath())) {
				this.sendError(exchange, 404, "Not Found");
				return;
			}

			final AnalyzeSupportRequest request = this.objectMapper.readValue(exchange.getRequestBody(),
					AnalyzeSupportRequest.class);
			final SupportAnalysis analysis = this.service.analyzeRequest(request.message());

			this.sendResponse(exchange, 200, analysis);

		} catch (final SupportAssistantException e) {
			this.sendError(exchange, e.getStatusCode(), e.getMessage());
		} catch (final Exception e) {
			this.sendError(exchange, 500, "An unexpected application failure occurred.");
			e.printStackTrace();
		} finally {
			exchange.close();
		}
	}

	private void sendResponse(HttpExchange exchange, int statusCode, Object body) throws IOException {
		final byte[] responseBytes = this.objectMapper.writeValueAsBytes(body);
		exchange.getResponseHeaders().set("Content-Type", "application/json");
		exchange.sendResponseHeaders(statusCode, responseBytes.length);
		try (final OutputStream os = exchange.getResponseBody()) {
			os.write(responseBytes);
		}
	}

	private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
		final Map<String, String> error = Map.of("error", message);
		this.sendResponse(exchange, statusCode, error);
	}
}
