package com.RAG_ETL_Pipeline.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;


import java.util.List;

@Service
public class RagServiceImpl implements RagService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());



    @Value("classpath:/prompts/user-message.st")
    private Resource userMessage;

    @Value("classpath:/prompts/system-message.st")
    private Resource systemMessage;


    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final DataLoader dataLoader;
    private final DataTransformer dataTransformer;

    public RagServiceImpl(
            ChatClient chatClient,
            VectorStore vectorStore,
            DataLoader dataLoader,
            DataTransformer dataTransformer) {

        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.dataLoader = dataLoader;
        this.dataTransformer = dataTransformer;
    }

    @Override
    public void saveData(List<String> list) {

        System.out.println("Inside saveData()");
        System.out.println("Number of chunks: " + list.size());

        List<Document> documentList =
                list.stream()
                        .map(Document::new)
                        .toList();

        vectorStore.add(documentList);

        System.out.println("Documents submitted to vector store");
    }


    @Override
    public String getResponse(String userQuery) {

        System.out.println("......Inside GettingResponse.....");



        var advisor = RetrievalAugmentationAdvisor.builder()

                .queryTransformers(
                        RewriteQueryTransformer.builder()
                                .chatClientBuilder(chatClient.mutate().clone())
                                .build(),
                        TranslationQueryTransformer.builder().chatClientBuilder(chatClient.mutate().clone()).targetLanguage("hindi").build()

                )
                .queryExpander(MultiQueryExpander.builder().chatClientBuilder(chatClient.mutate().clone()).numberOfQueries(3).build())
                .documentRetriever(
                        VectorStoreDocumentRetriever.builder()
                                .vectorStore(vectorStore)
                                .topK(3)
                                .similarityThreshold(0.3)
                                .build()
                )
                .documentJoiner(new ConcatenationDocumentJoiner())
                .queryAugmenter(ContextualQueryAugmenter.builder().build())
//                .documentPostProcessors()


                .build();


        //actual call to llm

        return chatClient
                .prompt()
                .advisors(advisor)
                .user(userQuery)
                .call()
                .content();
    }
    public void ingestPdf() {

        System.out.println("Starting PDF ingestion...");

        // Step 1: Read the PDF
        List<Document> documents = dataLoader.loadDocumentsFromPdf();

        System.out.println("PDF documents extracted: " + documents.size());

        if (documents.isEmpty()) {
            throw new IllegalStateException("No documents were extracted from cricket_rules.pdf"
            );
        }

        // Step 2: Split documents into chunks
        List<Document> chunks = dataTransformer.transform(documents);

        System.out.println("Chunks created: " + chunks.size());

        if (chunks.isEmpty()) {
            throw new IllegalStateException("No chunks were generated from the PDF");
        }

        // Step 3: Store the chunks and their embeddings
        vectorStore.add(chunks);

        System.out.println("PDF chunks submitted to the vector store.");
    }

}
