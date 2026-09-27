package org.example.supportassistantspringai.proxy;

import org.example.supportassistantspringai.domain.SupportAnalysis;
import org.example.supportassistantspringai.domain.SupportCategory;
import org.example.supportassistantspringai.domain.Urgency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SupportRequestProxyTest {

	private ChatClient chatClient;
	private SupportRequestProxy proxy;

    @BeforeEach
    void setUp() {
		this.chatClient = mock(ChatClient.class);
		final ChatClient.Builder chatClientBuilder = mock(ChatClient.Builder.class);
		when(chatClientBuilder.defaultSystem(anyString())).thenReturn(chatClientBuilder);
		when(chatClientBuilder.defaultAdvisors(any(SafeGuardAdvisor.class))).thenReturn(chatClientBuilder);
		when(chatClientBuilder.build()).thenReturn(this.chatClient);
		this.proxy = new SupportRequestProxy(chatClientBuilder);
		verify(chatClientBuilder).defaultAdvisors(any(SafeGuardAdvisor.class));
    }

    @Test
    void testAnalyzeRequestValidMessageDelegates() {
        final String message = "My account is locked";
        final SupportAnalysis expectedAnalysis = new SupportAnalysis("Account locked", SupportCategory.ACCOUNT,
                Urgency.HIGH, "We will help you unlock it.");

		final ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
		final ChatClient.CallResponseSpec responseSpec = mock(ChatClient.CallResponseSpec.class);
		when(this.chatClient.prompt()).thenReturn(requestSpec);
		when(requestSpec.user(message)).thenReturn(requestSpec);
		when(requestSpec.call()).thenReturn(responseSpec);
		when(responseSpec.entity(SupportAnalysis.class)).thenReturn(expectedAnalysis);

        final SupportAnalysis result = this.proxy.analyzeRequest(message);

        assertEquals(expectedAnalysis, result);
		verify(this.chatClient).prompt();
    }

    @Test
    void testEmptyMessageThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> this.proxy.analyzeRequest(""));
        assertThrows(IllegalArgumentException.class, () -> this.proxy.analyzeRequest(null));
        assertThrows(IllegalArgumentException.class, () -> this.proxy.analyzeRequest("   "));
		verifyNoInteractions(this.chatClient);
    }

}
