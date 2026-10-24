
package com.docmind.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class ChromaService {

    private final WebClient webClient;

    private static final String COLLECTION_ID =
            "8c0a87a1-b79d-4528-91e0-8055dd5ba063";

    public ChromaService() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8000")
                .build();
    }

    // Check ChromaDB connection
    public String checkConnection() {
        return webClient.get()
                .uri("/api/v2/heartbeat")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    // Create or get the existing collection
    public String createCollection() {
        return webClient.post()
                .uri("/api/v2/tenants/default_tenant/databases/default_database/collections")
                .header("Content-Type", "application/json")
                .bodyValue("""
                    {
                        "name": "docmind_documents",
                        "get_or_create": true
                    }
                    """)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    // Store PDF chunks and their embeddings
    public String storeChunks(
            List<String> chunks,
            List<float[]> embeddings) {

        if (chunks.size() != embeddings.size()) {
            throw new IllegalArgumentException(
                    "Chunks and embeddings must have equal sizes.");
        }

        List<String> ids = new ArrayList<>();
        List<List<Float>> vectors = new ArrayList<>();

        String uniqueId = java.util.UUID.randomUUID().toString();

        for (int i = 0; i < chunks.size(); i++) {
            ids.add(uniqueId + "_chunk_" + i);

            List<Float> vector = new ArrayList<>();

            for (float value : embeddings.get(i)) {
                vector.add(value);
            }

            vectors.add(vector);
        }

        Map<String, Object> request = new HashMap<>();
        request.put("ids", ids);
        request.put("documents", chunks);
        request.put("embeddings", vectors);

        return webClient.post()
                .uri("/api/v2/tenants/default_tenant/databases/default_database/collections/"
                        + COLLECTION_ID + "/add")
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    // Search for similar PDF chunks and return their distances
    public String searchSimilarChunks(
            float[] questionEmbedding,
            int numberOfResults) {

        List<Float> vector = new ArrayList<>();

        for (float value : questionEmbedding) {
            vector.add(value);
        }

        Map<String, Object> request = new HashMap<>();
        request.put("query_embeddings", List.of(vector));
        request.put("n_results", numberOfResults);

        // Request distances along with IDs and document text
        request.put("include",
                List.of("documents", "distances", "metadatas"));

        return webClient.post()
                .uri("/api/v2/tenants/default_tenant/databases/default_database/collections/"
                        + COLLECTION_ID + "/query")
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
