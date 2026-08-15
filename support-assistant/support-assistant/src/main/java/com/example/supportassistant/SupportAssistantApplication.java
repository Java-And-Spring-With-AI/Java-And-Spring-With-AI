package com.example.supportassistant;

import com.example.supportassistant.controller.SupportRequestController;
import com.example.supportassistant.model.OllamaModelProxy;
import com.example.supportassistant.service.SupportRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SupportAssistantApplication {

	static void main() throws IOException {
		final String baseUrl = System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://127.0.0.1:11434");
		final String model = System.getenv().getOrDefault("OLLAMA_MODEL", "llama3.2");
		final int port = Integer.parseInt(System.getenv().getOrDefault("SERVER_PORT", "9090"));

		final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule());

		final OllamaModelProxy modelProxy = new OllamaModelProxy(baseUrl, model, objectMapper);
		final SupportRequestService service = new SupportRequestService(modelProxy, objectMapper);
		final SupportRequestController controller = new SupportRequestController(service, objectMapper);

		final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/api/support/requests/analyze", controller);
		server.setExecutor(null);

		IO.println("Support Assistant Application started on port " + port);
		IO.println("Using Ollama model: " + model + " at " + baseUrl);
		server.start();
	}
}
