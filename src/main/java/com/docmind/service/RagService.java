
package com.docmind.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RagService {

    private final ChunkEmbeddingService chunkEmbeddingService;
    private final ChromaService chromaService;
    private final OllamaService ollamaService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Smaller distance generally means a closer match.
    // This is an initial threshold; tune it using actual results.
    private static final double MAX_DISTANCE = 0.8;

    public RagService(
            ChunkEmbeddingService chunkEmbeddingService,
            ChromaService chromaService,
            OllamaService ollamaService) {

        this.chunkEmbeddingService = chunkEmbeddingService;
        this.chromaService = chromaService;
        this.ollamaService = ollamaService;
    }

    // Store PDF chunks and their embeddings in ChromaDB
    public String storePdfText(String pdfText) throws Exception {

        List<String> chunks =
                chunkEmbeddingService.getChunks(pdfText);

        if (chunks.isEmpty()) {
            return "No text found in the PDF.";
        }

        List<float[]> embeddings =
                chunkEmbeddingService.generateChunkEmbeddings(pdfText);

        return chromaService.storeChunks(chunks, embeddings);
    }

    // Retrieve relevant chunks and generate an answer
    public String askQuestion(String question) throws Exception {

        float[] questionEmbedding =
                chunkEmbeddingService.generateQuestionEmbedding(question);

        String searchResult =
        chromaService.searchSimilarChunks(questionEmbedding, 5);
        System.out.println("Retrieved chunks from ChromaDB:");
        System.out.println(searchResult);

        JsonNode root = objectMapper.readTree(searchResult);

        JsonNode documents = root.path("documents").path(0);
        JsonNode ids = root.path("ids").path(0);
        JsonNode distances = root.path("distances").path(0);

        if (!documents.isArray() || documents.isEmpty()) {
            return "The answer is not available in the retrieved PDF content.";
        }

        StringBuilder context = new StringBuilder();
        StringBuilder sources = new StringBuilder();

        int relevantCount = 0;

        for (int i = 0; i < documents.size(); i++) {

            double distance = Double.MAX_VALUE;

            if (distances.isArray() && i < distances.size()
                    && distances.get(i).isNumber()) {
                distance = distances.get(i).asDouble();
            }

            System.out.println(
                    "Chunk " + (i + 1) + " distance: " + distance);

            // Skip chunks that are too far from the question.
            if (distance > MAX_DISTANCE) {
                continue;
            }

            relevantCount++;

            String chunkText = documents.get(i).asText();

            String chunkId = "Unknown";
            if (ids.isArray() && i < ids.size()) {
                chunkId = ids.get(i).asText();
            }

            context.append("Source ").append(relevantCount).append(":\n");
            context.append(chunkText).append("\n\n");

            sources.append("\n\nSource ").append(relevantCount)
                    .append(" (").append(chunkId)
                    .append(", distance: ")
                    .append(String.format("%.4f", distance))
                    .append("):\n");

            sources.append(chunkText.trim());
        }

        // If no chunks pass the threshold, don't use unrelated text.
        if (relevantCount == 0) {
            return "The answer is not available in the retrieved PDF content.";
        }

        String answer = ollamaService.askOllama(
                context.toString(),
                question
        );

        return answer + "\n\n--- Retrieved Source Text ---" + sources;
    }
}
