
package com.docmind.service;

import java.util.ArrayList;
import java.util.List;

public class DocumentChunker {

    private static final int CHUNK_SIZE = 500;
    private static final int CHUNK_OVERLAP = 50;

    public static List<String> splitIntoChunks(String text) {

        List<String> chunks = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return chunks;
        }

        int start = 0;

        while (start < text.length()) {

            int end = Math.min(start + CHUNK_SIZE, text.length());

            // Try to split at a sentence or word boundary.
            if (end < text.length()) {

                int sentenceEnd = text.lastIndexOf(". ", end);

                if (sentenceEnd > start + CHUNK_SIZE / 2) {
                    end = sentenceEnd + 1;
                } else {
                    int wordEnd = text.lastIndexOf(' ', end);

                    if (wordEnd > start + CHUNK_SIZE / 2) {
                        end = wordEnd;
                    }
                }
            }

            String chunk = text.substring(start, end).trim();

            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }

            if (end >= text.length()) {
                break;
            }

            // Move forward while retaining overlap.
            start = Math.max(start + 1, end - CHUNK_OVERLAP);
        }

        return chunks;
    }
}
