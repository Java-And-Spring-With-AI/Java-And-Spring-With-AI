package com.example.supportassistant.model;

import java.util.List;

public record OllamaRequest(String model, List<Message> messages, boolean stream) {
}
