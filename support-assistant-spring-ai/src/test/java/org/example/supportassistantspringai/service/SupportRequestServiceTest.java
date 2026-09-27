package org.example.supportassistantspringai.service;

import org.example.supportassistantspringai.domain.SupportAnalysis;
import org.example.supportassistantspringai.domain.SupportCategory;
import org.example.supportassistantspringai.domain.Urgency;
import org.example.supportassistantspringai.proxy.SupportRequestProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SupportRequestServiceTest {

	private SupportRequestProxy proxy;
	private SupportRequestService service;

	@BeforeEach
	void setUp() {
		this.proxy = mock(SupportRequestProxy.class);
		this.service = new SupportRequestService(this.proxy);
	}

	@Test
	void testAnalyzeRequestDelegatesToProxy() {
		final String message = "My account is locked";
		final SupportAnalysis expectedAnalysis = new SupportAnalysis("Account locked", SupportCategory.ACCOUNT,
				Urgency.HIGH, "We will help you unlock it.");
		when(this.proxy.analyzeRequest(message)).thenReturn(expectedAnalysis);

		final SupportAnalysis result = this.service.analyzeRequest(message);

		assertEquals(expectedAnalysis, result);
		verify(this.proxy).analyzeRequest(message);
	}
}
