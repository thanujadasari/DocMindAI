package com.docmind.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.clint.WebClint;

import java.util.Map;

@Service
public class GeminiService {
    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient=WebClient.create();

    public String askGemini(String pdfText, String question){
        String prompt= """
                you are an Ai assistant.
                
                Answer the user's question ONLY using following PDF content.
                
                PDF Content:
                %s
                
                Question:
                %s
                """.formatted(pdfText,question);
        Map<String, Object> body = Map.of(
                "contents", new Object[]{
                        Map.of(
                                "parts", new Object[]{
                                        Map.of("text", prompt)

                                }
                        )
                }
        );
        String response=webClient.post()
                .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" +apikey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return response;

    }

}
