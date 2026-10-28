package com.RAG_ETL_Pipeline.service;
import org.springframework.ai.document.Document;

import java.util.List;


public interface DataTransformer {
    List<Document> transform(List<Document> documents);

}
