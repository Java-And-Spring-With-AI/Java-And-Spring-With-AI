package org.example.supportassistantspringai.service;

import org.example.supportassistantspringai.domain.SupportAnalysis;
import org.example.supportassistantspringai.proxy.SupportRequestProxy;
import org.springframework.stereotype.Service;

@Service
public class SupportRequestService {

	private final SupportRequestProxy proxy;

	public SupportRequestService(SupportRequestProxy proxy) {
		this.proxy = proxy;
	}

	public SupportAnalysis analyzeRequest(String message) {
		return this.proxy.analyzeRequest(message);
	}
}
