package com.chen.rag.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AiExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleAiClientException(RuntimeException e) {
    return ResponseEntity.status(500)
            .body("AI服务错误: " + e.getCause() + "->" + e.getMessage());
  }
}