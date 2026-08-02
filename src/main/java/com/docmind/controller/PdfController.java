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

    public PdfController(HistoryService historyService,
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

            return "Upload failed: " + e.getMessage();

        }
    }

    @GetMapping("/ask")
    public AnswerResponse askQuestion(@RequestParam String question) {

        String pdfPath = fileStorageService.getUploadedFilePath();

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

        // Ask Gemini instead of keyword search
        String answer = geminiService.askGemini(
                document.getText(),
                question
        );

        // Save question & answer in database
        historyService.saveHistory(question, answer);

        return new AnswerResponse(question, answer);
    }
}