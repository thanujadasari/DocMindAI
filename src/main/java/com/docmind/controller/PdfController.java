
package com.docmind.controller;

import com.docmind.model.AnswerResponse;
import com.docmind.model.PdfDocument;
import com.docmind.service.FileStorageService;
import com.docmind.service.HistoryService;
import com.docmind.service.PdfService;
import com.docmind.service.RagService;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
public class PdfController {

    private final HistoryService historyService;
    private final FileStorageService fileStorageService;
    private final RagService ragService;

    public PdfController(
            HistoryService historyService,
            FileStorageService fileStorageService,
            RagService ragService) {

        this.historyService = historyService;
        this.fileStorageService = fileStorageService;
        this.ragService = ragService;
    }

    // Upload PDF and store its chunks in ChromaDB
    @PostMapping("/upload")
    public String uploadPdf(@RequestParam("file") MultipartFile file) {

        try {
            String filePath = fileStorageService.saveFile(file);

            PdfDocument document = PdfService.loadPdf(
                    filePath,
                    new File(filePath).getName()
            );

            String pdfText = document.getText();

            String result = ragService.storePdfText(pdfText);

            return "PDF uploaded successfully!\n"
                    + "Saved at: " + filePath + "\n"
                    + "ChromaDB result: " + result;

        } catch (Exception e) {
            e.printStackTrace();
            return "Upload failed: " + e.getMessage();
        }
    }

    // Ask a question using RAG
    @GetMapping("/ask")
    public AnswerResponse askQuestion(
            @RequestParam String question) {

        try {
            String answer = ragService.askQuestion(question);

            historyService.saveHistory(question, answer);

            return new AnswerResponse(question, answer);

        } catch (Exception e) {
            e.printStackTrace();

            return new AnswerResponse(
                    question,
                    "ERROR: " + e.getMessage()
            );
        }
    }
}
