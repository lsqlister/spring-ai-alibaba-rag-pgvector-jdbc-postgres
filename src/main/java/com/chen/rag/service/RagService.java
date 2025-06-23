package com.chen.rag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagService {

  private final ChatClient chatClient;
  private final VectorStore vectorStore;
  private final DocumentService documentService;

  @Value("classpath:/prompts/rag-prompt-template.st")
  private Resource ragPromptTemplate;

  public String processQuery(String query) {
    // 1. 检索相关文档
    List<Document> similarDocuments = vectorStore.similaritySearch(query);
    System.out.println("检索到相关文档：" + similarDocuments.size());

    // 2. 构建上下文
    String context = similarDocuments.stream()
            .map(Document::getFormattedContent)
            .collect(Collectors.joining("\n\n"));

    // 3. 构建提示词
    PromptTemplate promptTemplate = new PromptTemplate(ragPromptTemplate);
    Prompt prompt = promptTemplate.create(Map.of(
            "current_date", new Date().toLocaleString(),
            "input", query,
            "context", context
    ));
    // 4. 调用LLM生成回答
    return chatClient.prompt(prompt).call().content();
  }

  public void initKnowledgeBase(String filePath) throws IOException {
    documentService.loadAndStoreDocuments(filePath);
  }
}