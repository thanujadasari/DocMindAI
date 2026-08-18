package com.docmind.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.create();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String askGemini(String pdfText, String question) {

        String prompt = """
                You are an AI assistant.

                Answer the user's question ONLY using the following PDF content.

                If the answer is not present in the PDF, say:
                "The answer is not available in the uploaded PDF."

                PDF Content:
                %s

                Question:
                %s
                """.formatted(pdfText, question);

        Map<String, Object> body = Map.of(
                "contents", new Object[]{
                        Map.of(
                                "role", "user",
                                "parts", new Object[]{
                                        Map.of("text", prompt)
                                }
                        )
                }
        );

        try {

            String result = webClient.post()
                    .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent")
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            errorResponse -> errorResponse
                                    .bodyToMono(String.class)
                                    .map(errorBody ->
                                            new RuntimeException(
                                                    "Gemini API Error: " + errorBody
                                            )
                                    )
                    )
                    .bodyToMono(String.class)
                    .block();

            // Convert Gemini JSON response into a JSON tree
            JsonNode root = objectMapper.readTree(result);

            // Extract only the actual answer
            JsonNode textNode = root
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            if (textNode.isMissingNode()) {
                return "Unable to extract an answer from Gemini.";
            }

            return textNode.asText();

        } catch (Exception e) {

            System.out.println("========== GEMINI ERROR ==========");
            e.printStackTrace();
            System.out.println("===================================");

            throw new RuntimeException(
                    "Gemini request failed: " + e.getMessage()
            );
        }
    }
}