package com.docmind.controller;

import com.docmind.model.PdfDocument;
import com.docmind.service.PdfService;
import com.docmind.service.QuestionService;
import com.docmind.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class PdfController {

    @GetMapping("/ask")
    public ArrayList<String> askQuestion(@RequestParam String question) {

        String pdfPath = "src/main/resources/Programming_QA_Test.pdf";

        PdfDocument document = PdfService.loadPdf(
                pdfPath,
                "Programming_QA_Test.pdf"
        );

        ArrayList<String> keywords =
                QuestionService.extractKeywords(question);

        return SearchService.searchKeywords(
                document.getText(),
                keywords
        );
    }
}