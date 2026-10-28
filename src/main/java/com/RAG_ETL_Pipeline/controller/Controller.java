package com.RAG_ETL_Pipeline.controller;

import com.RAG_ETL_Pipeline.service.RagService;
import com.RAG_ETL_Pipeline.service.RagServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class Controller {

    private RagService ragService;
    @Autowired
    private RagServiceImpl ragServiceImpl;

    public Controller(RagService ragService) {
        this.ragService = ragService;
    }


    @PostMapping("/chat")
    public ResponseEntity<String> getResponse(@RequestParam("q") String userQuery) {
        System.out.println("......Inside Chat Controller.....");

        return ResponseEntity.ok(ragService.getResponse(userQuery));
    }
    @PostMapping("/ingest/pdf")
    public ResponseEntity<String> ingestPdf() {

        ragServiceImpl.ingestPdf();

        return ResponseEntity.ok("PDF ingestion completed. Check the database.");
    }
}

