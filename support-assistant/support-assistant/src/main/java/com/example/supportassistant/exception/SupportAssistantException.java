package com.example.supportassistant.exception;

public class SupportAssistantException extends RuntimeException {
	private final int statusCode;

	public SupportAssistantException(String message, int statusCode) {
		super(message);
		this.statusCode = statusCode;
	}

	public SupportAssistantException(String message, int statusCode, Throwable cause) {
		super(message, cause);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return this.statusCode;
	}
}
