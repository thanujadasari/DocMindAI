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
        System.out.println(document.getText());


        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your question:");

        String question = scanner.nextLine();

        ArrayList<String> keywords = QuestionService.extractKeywords(question);
        ArrayList<String> results=SearchService.searchKeywords(document.getText(),keywords);
        if(results.isEmpty()){
            System.out.println("\nNo matching information found");

        }else{
            System.out.println("\nRelevant Information:\n");
            for(String line: results){
                System.out.println(line);
            }
        }



        scanner.close();
    }
}