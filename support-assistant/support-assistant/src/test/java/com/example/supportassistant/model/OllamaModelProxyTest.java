package com.example.supportassistant.model;

import com.example.supportassistant.exception.ModelProxyException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

class OllamaModelProxyTest {

	private HttpServer server;
	private OllamaModelProxy proxy;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private int port;

	private String lastRequestBody;
	private int responseStatus = 200;
	private String responseBody = "";

	@BeforeEach
	void setUp() throws IOException {
		this.server = HttpServer.create(new InetSocketAddress(0), 0);
		this.port = this.server.getAddress().getPort();
		this.server.createContext("/api/chat", exchange -> {
			this.lastRequestBody = new String(exchange.getRequestBody().readAllBytes());
			exchange.sendResponseHeaders(this.responseStatus, this.responseBody.length());
			try (final OutputStream os = exchange.getResponseBody()) {
				os.write(this.responseBody.getBytes());
			}
		});
		this.server.start();

		this.proxy = new OllamaModelProxy("http://localhost:" + this.port, "llama3.2", this.objectMapper);
	}

	@AfterEach
	void tearDown() {
		this.server.stop(0);
	}

	@Test
	void shouldExtractMessageContentFromOllamaResponse() {
		this.responseBody = """
				{
				  "message": {
				    "role": "assistant",
				    "content": "Analysis result"
				  }
				}
				""";

		final String result = this.proxy.generate("sys", "user");

		assertEquals("Analysis result", result);
		assertTrue(this.lastRequestBody.contains("\"model\":\"llama3.2\""));
		assertTrue(this.lastRequestBody.contains("\"content\":\"sys\""));
		assertTrue(this.lastRequestBody.contains("\"content\":\"user\""));
	}

	@Test
	void shouldHandleUnsuccessfulOllamaHttpResponse() {
		this.responseStatus = 500;

		final ModelProxyException exception = assertThrows(ModelProxyException.class,
				() -> this.proxy.generate("sys", "user"));

		assertTrue(exception.getMessage().contains("Ollama returned an unsuccessful status: 500"));
	}
}
