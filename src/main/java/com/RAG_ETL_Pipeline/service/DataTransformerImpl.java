package com.RAG_ETL_Pipeline.service;

import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;
import org.springframework.ai.document.Document;

import java.util.List;


@Service
public class DataTransformerImpl implements DataTransformer {
    @Override
    public List<Document> transform(List<Document> documents) {
        System.out.println("......Inside the data transformer method.....");

        var splitter = TokenTextSplitter.builder()
                .withChunkSize(300)
                .withMinChunkSizeChars(400)
                .withMinChunkLengthToEmbed(10)
                .withMaxNumChunks(5000)
                .withKeepSeparator(true)
                .build();

        return splitter.transform(documents);
    }
}
