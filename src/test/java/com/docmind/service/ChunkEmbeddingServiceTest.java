
package com.docmind.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChunkEmbeddingServiceTest {

    @Test
    void shouldGenerateEmbeddingForEachChunk() throws Exception {

        ChunkEmbeddingService service = new ChunkEmbeddingService();

        String text = "Java is a programming language. ".repeat(50);

        List<float[]> embeddings = service.generateChunkEmbeddings(text);

        assertFalse(embeddings.isEmpty());

        for (float[] vector : embeddings) {
            assertEquals(384, vector.length);
        }

        System.out.println("All chunks have valid embeddings!");
    }
}
