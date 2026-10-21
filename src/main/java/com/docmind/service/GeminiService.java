
package com.docmind.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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

                Answer the user's question ONLY using
                the following PDF content.

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

        String url =
                "https://generativelanguage.googleapis.com/v1beta/"
                + "models/gemini-3.6-flash:generateContent";

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                String result = webClient.post()
                        .uri(url)
                        .header("x-goog-api-key", apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                JsonNode root = objectMapper.readTree(result);

                JsonNode textNode = root.path("candidates")
                        .path(0)
                        .path("content")
                        .path("parts")
                        .path(0)
                        .path("text");

                if (textNode.isMissingNode()) {
                    return "Gemini did not return an answer. "
                            + "Please try again.";
                }

                return textNode.asText();

            } catch (WebClientResponseException e) {

                int status = e.getStatusCode().value();

                // Retry only temporary server/rate-limit errors
                if ((status == 503 || status == 502
                        || status == 504 || status == 429)
                        && attempt < 3) {

                    System.out.println(
                            "Gemini temporary error: " + status
                            + ". Retry " + (attempt + 1) + "/3"
                    );

                    try {
                        Thread.sleep(1000L * (1L << (attempt - 1)));
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(
                                "Gemini retry interrupted."
                        );
                    }

                    continue;
                }

                throw new RuntimeException(
                        "Gemini API returned HTTP " + status
                        + ". Please try again later."
                );

            } catch (Exception e) {
                throw new RuntimeException(
                        "Unable to process the Gemini response: "
                        + e.getMessage(), e
                );
            }
        }

        throw new RuntimeException(
                "Gemini is temporarily unavailable after 3 attempts. "
                + "Please try again later."
        );
    }
}
