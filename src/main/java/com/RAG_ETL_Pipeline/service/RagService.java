package com.RAG_ETL_Pipeline.service;

import java.util.List;

public interface RagService {
    String getResponse(String userQuery);

    void saveData(List<String> list);
}
