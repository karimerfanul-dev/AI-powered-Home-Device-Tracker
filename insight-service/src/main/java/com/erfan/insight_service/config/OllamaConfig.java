package com.erfan.insight_service.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OllamaConfig {
    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("You are an expert energy efficiency advisor." +
                        "provide concise and practical advice to users on how to reduce " +
                        "their energy consumption based on their usage pattern.")
                .build();
    }
}
