# Spring AI 基于 PostgreSQL 向量数据库RAG 完整案例

下面是一个基于 Spring AI 1.0.0,Spring AI Alibaba1.0.0.2 版本，使用 **通义千问** 实现 RAG存储到PostgreSQL 数据库 的完整案例。

## 1.相关概念

### 1.1. 向量化

* 向量数据库（Vector Database）是一种以数学向量的形式存储数据集合的数据库，通过一个数字列表来表示维度空间中的一个位置。在这里，**向量数据库的功能是可以基于相似性搜索进行识别，而不是精准匹配**。

* 比如说在使用一个地理位置系统的向量数据库进行查询的时候，具体取决于模型的知识库和上下文理解能力

* 用户输入“北京”，其可能返回的结果会是 “中国、北京、华北、首都、奥运会” 等信息；

* 而输入“成都”，其返回结果可能会是“四川、平原、熊猫、火锅”等信息。

* 当然，返回的信息取决于向量数据库中存在的数据。用户可以通过参数的设置来限定返回的情况，进而适配不同的需求。

* 嵌入模型（Embedding Model）和向量数据库（Vector Database/Vector Store）是一对亲密无间的合作伙伴，也是 AI 技术栈中紧密关联的两大核心组件，两者的协同作用构成了现代语义搜索、推荐系统和 RAG（Retrieval Augmented Generation，检索增强生成）等应用的技术基础。

### 1.2. RAG

#### 1.2.1.RAG的基本概念

​	**RAG**，全称 **Retrieval-Augmented Generation** ，中文叫做**检索增强生成**。RAG是一种结合了检索系统和生成模型的新型技术框架，

#### 1.2.2.主要目的

- 充分利用外部企业自己的知识库
- 帮助大模型生成**更加准确、有依据、最新的回答**

​	通过使用RAG，解决了传统LLM存在的两个主要问题：

- **知识局限性**：LLM的知识被固定在训练数据中，无法知道最新消息。
- **幻觉现象**：LLM有时候会编造出并不存在的答案。

​	通过检索外部知识，RAG让模型突破了知识局限性，也让LLM（大语言模型）的幻觉现象得到解决。

## 2. 环境准备

### 2.1. 依赖配置 (pom.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.4.0</version>
    <relativePath/> <!-- lookup parent from repository -->
  </parent>

  <groupId>com.example</groupId>
  <artifactId>spring-ai-alibaba-rag-pgvector-jdbc-postgres</artifactId>
  <version>1.0.0-SNAPSHOT</version>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <java.version>17</java.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <!-- Spring Boot -->
    <spring-boot.version>3.4.0</spring-boot.version>
    <!-- Spring AI -->
    <spring-ai.version>1.0.0</spring-ai.version>
    <!-- Spring Alibaba AI -->
    <spring-ai-alibaba.version>1.0.0.2</spring-ai-alibaba.version>
  </properties>

  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>${spring-boot.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
      <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-bom</artifactId>
        <version>${spring-ai.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <dependencies>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
      <groupId>org.springframework.ai</groupId>
      <artifactId>spring-ai-autoconfigure-model-openai</artifactId>
    </dependency>

    <dependency>
      <groupId>org.springframework.ai</groupId>
      <artifactId>spring-ai-autoconfigure-model-chat-client</artifactId>
    </dependency>

    <dependency>
      <groupId>org.springframework.ai</groupId>
      <artifactId>spring-ai-pdf-document-reader</artifactId>
    </dependency>

    <dependency>
      <groupId>org.springframework.ai</groupId>
      <artifactId>spring-ai-pgvector-store</artifactId>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
      <groupId>org.postgresql</groupId>
      <artifactId>postgresql</artifactId>
    </dependency>

    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <scope>provided</scope>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>

  </dependencies>

  <repositories>
    <repository>
      <id>spring-milestones</id>
      <name>Spring Milestones</name>
      <url>https://repo.spring.io/milestone</url>
      <snapshots>
        <enabled>false</enabled>
      </snapshots>
    </repository>
    <repository>
      <id>spring-snapshots</id>
      <name>Spring Snapshots</name>
      <url>https://repo.spring.io/snapshot</url>
      <releases>
        <enabled>false</enabled>
      </releases>
    </repository>
    <repository>
      <id>aliyunmaven</id>
      <name>aliyun</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </repository>
  </repositories>

  <pluginRepositories>
    <pluginRepository>
      <id>public</id>
      <name>aliyun nexus</name>
      <url>https://maven.aliyun.com/repository/apache-snapshots</url>
      <releases>
        <enabled>true</enabled>
      </releases>
      <snapshots>
        <enabled>false</enabled>
      </snapshots>
    </pluginRepository>
  </pluginRepositories>

  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>

</project>
```

### 2.2. 配置文件 (application.yml)

```yaml
server:
  port: 8088

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/rag_demo
    username: postgres
    password: postgres
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
    defer-datasource-initialization: true  # 允许先创建扩展再初始化表
  ai:
    openai:
      api-key: ${DASHSCOPE_API_KEY}
      base-url: https://dashscope.aliyuncs.com/compatible-mode
      chat:
        options:
          model: qwen-max
      embedding:
        options:
          model: text-embedding-v1

# 打印日志
logging:
  level:
    com.example: DEBUG
    #org.springframework.ai.reader.pdf: DEBUG
    #org.springframework.jdbc.core.JdbcTemplate: DEBUG  # 显示 SQL 语句
    #org.springframework.jdbc.core.StatementCreatorUtils: TRACE  # 显示参数绑定
```

## 3. 核心实现

### 3.1. 向量存储配置类

```java
package com.chen.rag.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class VectorStoreConfig {

  @Bean
  public VectorStore vectorStore(EmbeddingModel embeddingClient, JdbcTemplate jdbcTemplate) {
    return PgVectorStore.builder(jdbcTemplate, embeddingClient).build();
  }

  @Bean
  public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
```

### 3.2. 文档加载与处理

```java
package com.chen.rag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

  private final VectorStore vectorStore;

  public void loadAndStoreDocuments(String filePath) throws IOException {
    // PDF文档读取配置
    PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
            .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder()
                    .withNumberOfTopTextLinesToDelete(0)
                    .build())
            .build();

    //先使用目录分段读取方式读取PDF并分段落
    Resource resource = new FileSystemResource(filePath);
    ParagraphPdfDocumentReader pdfReader = new ParagraphPdfDocumentReader(resource, config);
    List<Document> documents = pdfReader.get();
    
    System.out.println("Total number of documents1: " + documents.size());
    if (documents.isEmpty()) {
      //如果沒有获取到目录,在改用分页方式拆分
      PagePdfDocumentReader pdfReader2 = new PagePdfDocumentReader(resource);
      documents = pdfReader2.get();
      System.out.println("Total number of documents2: " + documents.size());
    }

    // 文本分割
    TokenTextSplitter splitter = new TokenTextSplitter();
    List<Document> splitDocs = splitter.apply(documents);

    // 存储到向量数据库
    vectorStore.add(splitDocs);
  }
}
```

### 3.3. RAG 服务实现

```java
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
```

### 3.4. 提示词模板 (resources/prompts/rag-prompt-template.st)

```
当前日期：{current_date}

请基于以下上下文信息回答问题。请遵循以下规则：
1. 回答要专业、准确
2. 如果上下文不包含答案，请明确说明"根据提供的信息无法确定"
3. 使用中文回答
4. 保持回答简洁明了

上下文：
{context}

问题：{input}

请按以下格式回答：
【回答】: (你的回答)
【来源】: (指出回答基于哪些上下文片段，用1,2,3编号)
```

## 3. REST 接口

```java
package com.chen.rag.controller;

import com.chen.rag.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/rag")
@RequiredArgsConstructor
public class RagController {

  private final RagService ragService;

  @PostMapping("/init")
  public String initKnowledgeBase(@RequestParam String filePath) throws IOException {
    ragService.initKnowledgeBase(filePath);
    return "知识库初始化完成";
  }

  //http://localhost:8088/rag/query?query=文档中所有内容
  @GetMapping("/query")
  public String processQuery(String query) {
    return ragService.processQuery(query);
  }
}
```

## 4. 测试案例

### 4.1.测试代码

```java
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
    // 初始化知识库
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
```

### 4.2.测试文件sample.pdf

```pdf
Spring AI 技术文档

核心功能：

1. 支持多种大语言模型集成（如Ollama）
2. 提供RAG（检索增强生成）完整流程
3. 内置文档处理工具（PDF/Word解析）
4. 向量存储与检索能力

版本要求：Spring AI 1.0.0
```

## 5. 测试结果

```java
Total number of documents1: 0
Total number of documents2: 1
检索到相关文档：2
AI回答: 【回答】: RAG是指检索增强生成（Retrieval-Augmented Generation），它是一种结合了信息检索和文本生成的技术，能够基于外部知识库或文档来增强语言模型的输出质量。
【来源】: 1, 2
```

## 6. PostgreSQL 向量库

需要在 PostgreSQL 服务器上安装 pgvector 扩展

### 6.1.创建向量表

```sql
-- 创建 vector_store 表（根据你的应用需求调整列定义）
CREATE TABLE IF NOT EXISTS public.vector_store (
    id UUID  PRIMARY KEY,
		-- 根据你的向量维度调整 通义千问1536,深度求索2048
    embedding VECTOR(1536),  
    content TEXT,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 6.2.执行测试后的结果

![image1](assets\image1.png)

## 7.小结

这个案例展示了完整的RAG实现流程，包含文档加载、向量存储、检索和生成四个核心环节。您可以根据实际需求调整各部分实现，例如替换向量数据库（改用Redis或Chroma）、尝试不同的大模型或者本地Ollama模型，或者优化提示词模板。
