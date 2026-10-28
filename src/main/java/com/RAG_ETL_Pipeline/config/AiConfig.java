package com.RAG_ETL_Pipeline.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AiConfig {

    private final Logger logger = LoggerFactory.getLogger(AiConfig.class);



    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        new SafeGuardAdvisor(List.of("games"))
                )
                .build();
    }
}
