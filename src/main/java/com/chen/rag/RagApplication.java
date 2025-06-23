package com.chen.rag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RagApplication {

  public static void main(String[] args) {
    SpringApplication.run(RagApplication.class, args);
    System.out.println("启动完成");
  }

}
