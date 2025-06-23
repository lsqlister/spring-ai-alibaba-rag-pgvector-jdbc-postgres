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