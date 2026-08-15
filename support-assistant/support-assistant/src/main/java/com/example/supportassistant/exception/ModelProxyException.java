package com.example.supportassistant.exception;

public class ModelProxyException extends RuntimeException {
	public ModelProxyException(String message) {
		super(message);
	}

	public ModelProxyException(String message, Throwable cause) {
		super(message, cause);
	}
}
