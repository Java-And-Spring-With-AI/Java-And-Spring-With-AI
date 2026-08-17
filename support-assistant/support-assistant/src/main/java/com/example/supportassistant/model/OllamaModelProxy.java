package com.example.supportassistant.model;

import com.example.supportassistant.exception.ModelProxyException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class OllamaModelProxy implements ModelProxy {

	private final String baseUrl;
	private final String model;
	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;

	public OllamaModelProxy(String baseUrl, String model, ObjectMapper objectMapper) {
		this.baseUrl = baseUrl;
		this.model = model;
		this.httpClient = HttpClient.newHttpClient();
		this.objectMapper = objectMapper;
	}

	@Override
	public String generate(String systemInstruction, String userInput) {
		try {
			final HttpRequest httpRequest = this.buildRequest(systemInstruction, userInput);
			final HttpResponse<String> response = this.sendRequest(httpRequest);
			return this.handleResponse(response);
		} catch (final IOException | InterruptedException | RuntimeException e) {
			throw new ModelProxyException("Failed to call Ollama: " + e.getMessage(), e);
		}
	}

	private HttpRequest buildRequest(String systemInstruction, String userInput) throws IOException {
		final OllamaRequest request = new OllamaRequest(this.model,
				List.of(new Message("system", systemInstruction), new Message("user", userInput)), false);

		final String requestBody = this.objectMapper.writeValueAsString(request);

		return HttpRequest.newBuilder().uri(URI.create(this.baseUrl + "/api/chat"))
				.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();
	}

	private HttpResponse<String> sendRequest(HttpRequest httpRequest) throws IOException, InterruptedException {
		return this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
	}

	private String handleResponse(HttpResponse<String> response) throws IOException {
		if (response.statusCode() != 200) {
			throw new ModelProxyException("Ollama returned an unsuccessful status: " + response.statusCode());
		}

		final OllamaResponse ollamaResponse = this.objectMapper.readValue(response.body(), OllamaResponse.class);
		return ollamaResponse.message().content();
	}
}
