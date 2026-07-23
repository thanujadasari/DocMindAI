package com.docmind.controller;

import com.docmind.model.PdfDocument;
import com.docmind.service.PdfService;
import com.docmind.service.QuestionService;
import com.docmind.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.docmind.model.AnswerResponse;
import com.docmind.service.HistoryService;


import java.util.ArrayList;

@RestController
public class PdfController {
    private final HistoryService historyService;

    public PdfController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/ask")
    public AnswerResponse askQuestion(@RequestParam String question) {

        String pdfPath = "src/main/resources/Community Connect Form.pdf.pdf";

        PdfDocument document = PdfService.loadPdf(
                pdfPath,
                "Community Connect Form.pdf.pdf"
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