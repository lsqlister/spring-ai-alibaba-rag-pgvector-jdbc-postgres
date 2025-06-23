package com.chen.rag.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {
  @Bean
  public ChatClient chatClient(ChatClient.Builder builder) {
    return builder.defaultAdvisors(new SimpleLoggerAdvisor()).build();
  }

}
