
package com.docmind.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmbeddingServiceTest {

    @Test
    void shouldGenerateEmbedding() throws Exception {
        EmbeddingService service = new EmbeddingService();

        float[] vector = service.generateEmbedding(
                "Java is a programming language."
        );

        assertNotNull(vector);
        assertEquals(384, vector.length);

        System.out.println("Vector dimensions: " + vector.length);
        System.out.println("First 5 values:");

        for (int i = 0; i < 5; i++) {
            System.out.println(vector[i]);
        }
    }
}
