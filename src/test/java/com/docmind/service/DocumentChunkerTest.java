
package com.docmind.service;


import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DocumentChunkerTest {

    @Test
    void splitsLongTextIntoOverlappingChunks() {

        String text = "A".repeat(1200);

        List<String> chunks =
                DocumentChunker.splitIntoChunks(text);

        assertEquals(3, chunks.size());
        assertEquals(500, chunks.get(0).length());
        assertEquals(500, chunks.get(1).length());
        assertEquals(300, chunks.get(2).length());

        assertEquals(
                chunks.get(0).substring(450),
                chunks.get(1).substring(0, 50)
        );
    }

    @Test
    void returnsEmptyListForBlankText() {

        List<String> chunks =
                DocumentChunker.splitIntoChunks("   ");

        assertTrue(chunks.isEmpty());
    }
}
