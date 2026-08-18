package com.docmind;

import com.docmind.model.PdfDocument;
import com.docmind.service.PdfService;
import com.docmind.service.QuestionService;
import com.docmind.service.SearchService;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        System.out.println("Welcome to DocMind AI");

        String pdfPath = "src/main/resources/Community Connect Form.pdf.pdf";

        PdfDocument document = PdfService.loadPdf(
                pdfPath,
                "Community Connect Form.pdf.pdf"
        );

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your question:");

        String question = scanner.nextLine();

        ArrayList<String> keywords =
                QuestionService.extractKeywords(question);

        String result =
                SearchService.searchAnswer(document.getText(), keywords);

        System.out.println("\nRelevant Information:\n");
        System.out.println(result);

        scanner.close();
    }
}