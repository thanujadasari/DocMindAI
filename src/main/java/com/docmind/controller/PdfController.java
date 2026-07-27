package com.docmind.controller;

import com.docmind.model.AnswerResponse;
import com.docmind.model.PdfDocument;
import com.docmind.service.FileStorageService;
import com.docmind.service.HistoryService;
import com.docmind.service.PdfService;
import com.docmind.service.QuestionService;
import com.docmind.service.SearchService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;

@RestController
public class PdfController {

    private final HistoryService historyService;
    private final FileStorageService fileStorageService;

    public PdfController(HistoryService historyService,
                         FileStorageService fileStorageService) {

        this.historyService = historyService;
        this.fileStorageService = fileStorageService;
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

        ArrayList<String> keywords =
                QuestionService.extractKeywords(question);

        String answer = SearchService.searchAnswer(
                document.getText(),
                keywords
        );

        historyService.saveHistory(question, answer);

        return new AnswerResponse(question, answer);
    }
}