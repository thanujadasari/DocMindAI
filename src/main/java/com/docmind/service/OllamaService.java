
package com.docmind.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class OllamaService {

    private final WebClient webClient = WebClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String askOllama(String pdfText, String question) {

        String prompt = """
                You are a helpful AI assistant in a PDF question-answering system.

                Follow these rules carefully:

                1. Check whether the retrieved PDF content answers the question.
                2. If the answer is present, answer using only information
                   supported by the retrieved PDF content.
                3. Do not add unsupported facts or assumptions.
                4. If the answer is not present, say:
                   "The answer is not available in the retrieved PDF content."
                5. Do not guess or invent information.
                6. Answer in simple, clear English.

                Retrieved PDF Content:
                %s

                User Question:
                %s

                Answer:
                """.formatted(pdfText, question);

        Map<String, Object> body = Map.of(
                "model", "qwen2.5:3b",
                "prompt", prompt,
                "stream", false
        );

        String result = webClient.post()
                .uri("http://localhost:11434/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JsonNode root = objectMapper.readTree(result);
            return root.path("response").asText();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to process the Ollama response.", e);
        }
    }
}
