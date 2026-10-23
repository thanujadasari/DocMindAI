
package com.docmind.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChunkEmbeddingService {

    private final EmbeddingService embeddingService;

    public ChunkEmbeddingService() throws Exception {
        this.embeddingService = new EmbeddingService();
    }

    // Generate embeddings for PDF chunks
    public List<float[]> generateChunkEmbeddings(String pdfText)
            throws Exception {

        List<String> chunks = getChunks(pdfText);
        List<float[]> embeddings = new ArrayList<>();

        for (String chunk : chunks) {
            embeddings.add(
                    embeddingService.generateEmbedding(chunk)
            );
        }

        System.out.println("Total chunks: " + chunks.size());
        System.out.println("Total embeddings: " + embeddings.size());

        return embeddings;
    }

    // Split PDF text into chunks
    public List<String> getChunks(String pdfText) {
        return DocumentChunker.splitIntoChunks(pdfText);
    }

    // Generate an embedding for a user's question
    public float[] generateQuestionEmbedding(String question)
            throws Exception {

        return embeddingService.generateEmbedding(question);
    }
}
