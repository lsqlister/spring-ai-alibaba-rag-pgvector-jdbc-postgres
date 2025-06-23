package com.chen.rag;

import com.chen.rag.service.RagService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class RagServiceTest {

  @Resource
  private RagService ragService;

  @BeforeEach
  void initKnowledgeBase() throws IOException {
    // 直接从类路径根目录引用
    ClassPathResource resource = new ClassPathResource("sample.pdf");
    String filePath = resource.getFile().getAbsoluteFile().toString();
    System.out.println(filePath);
    // 初始化知识库 (实际项目应该用@BeforeEach)
    ragService.initKnowledgeBase(filePath);
  }

  @Test
  void testRagPipeline() {
    String query = "请帮我总结一下pdf文件的内容";
    query = "文档中提到的主要技术是什么？";
    //query = "文档中所有内容？";
    query = "RAG是什么？";
    // 测试查询
    String response = ragService.processQuery(query);
    assertNotNull(response);
    assertFalse(response.contains("根据现有信息无法确定"));
    System.out.println("AI回答: " + response);
  }
}