package com.docmind.controller;

import com.docmind.model.AnswerResponse;
import com.docmind.model.PdfDocument;
import com.docmind.service.FileStorageService;
import com.docmind.service.GeminiService;
import com.docmind.service.HistoryService;
import com.docmind.service.PdfService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
public class PdfController {

    private final HistoryService historyService;
    private final FileStorageService fileStorageService;
    private final GeminiService geminiService;

    public PdfController(
            HistoryService historyService,
            FileStorageService fileStorageService,
            GeminiService geminiService) {

        this.historyService = historyService;
        this.fileStorageService = fileStorageService;
        this.geminiService = geminiService;
    }

    @PostMapping("/upload")
    public String uploadPdf(@RequestParam("file") MultipartFile file) {

        try {

            String filePath = fileStorageService.saveFile(file);

            return "PDF uploaded successfully!\nSaved at: " + filePath;

        } catch (Exception e) {

            e.printStackTrace();

            return "Upload failed: " + e.getMessage();
        }
    }

    @GetMapping("/ask")
    public AnswerResponse askQuestion(@RequestParam String question) {

        try {

            System.out.println("========== ASK REQUEST ==========");
            System.out.println("Question: " + question);

            String pdfPath = fileStorageService.getUploadedFilePath();

            System.out.println("PDF Path: " + pdfPath);

            if (pdfPath == null) {

                return new AnswerResponse(
                        question,
                        "Please upload a PDF first."
                );
            }

            PdfDocument document = PdfService.loadPdf(
                    pdfPath,
                    new File(pdfPath).getName()
            );

            String pdfText = document.getText();

            System.out.println("PDF text length: " + pdfText.length());

            System.out.println("Calling Gemini...");

            String answer = geminiService.askGemini(
                    pdfText,
                    question
            );

            System.out.println("Gemini response received.");

            // Temporarily save history after Gemini succeeds
            historyService.saveHistory(question, answer);

            System.out.println("History saved successfully.");
            System.out.println("================================");

            return new AnswerResponse(question, answer);

        } catch (Exception e) {

            System.out.println("========== ASK ERROR ==========");
            e.printStackTrace();
            System.out.println("===============================");

            return new AnswerResponse(
                    question,
                    "ERROR: " + e.getMessage()
            );
        }
    }
}