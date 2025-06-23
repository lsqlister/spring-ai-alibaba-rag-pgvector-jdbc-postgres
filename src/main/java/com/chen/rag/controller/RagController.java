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