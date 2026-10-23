
package com.docmind.service;

import org.springframework.stereotype.Service;

import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RagService {

    private final ChunkEmbeddingService chunkEmbeddingService;
    private final ChromaService chromaService;
    private final OllamaService ollamaService;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    // Retrieve relevant PDF chunks and answer the question
    public String askQuestion(String question) throws Exception {

        float[] questionEmbedding =
                chunkEmbeddingService.generateQuestionEmbedding(question);

        String searchResult =
                chromaService.searchSimilarChunks(questionEmbedding, 3);

        JsonNode root = objectMapper.readTree(searchResult);
        JsonNode documents = root.path("documents").path(0);

        if (!documents.isArray() || documents.isEmpty()) {
            return "The answer is not available in the uploaded PDF.";
        }

        StringBuilder context = new StringBuilder();

        for (JsonNode document : documents) {
            context.append(document.asText()).append("\n\n");
        }

        return ollamaService.askOllama(
                context.toString(),
                question
        );
    }
}
