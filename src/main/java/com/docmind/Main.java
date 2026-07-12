package com.docmind;
import com.docmind.utils.PdfReaderUtil;
import java.util.Scanner;
import com.docmind.service.SearchService;
import java.util.ArrayList;
import com.docmind.model.PdfDocument;
import com.docmind.service.PdfService;

public class Main {
    public static void main(String[] args){
        System.out.println("Welcome to DocMind AI");
        String pdfPath = "src/main/resources/Community Connect Form.pdf.pdf";
        PdfDocument document=

                PdfService.loadPdf(
                        pdfPath,
                        "Community Connect Form.pdf.pdf"
                );

        String[] lines=document.getText().split("\n");

        Scanner scanner= new Scanner(System.in);
        System.out.println("Enter a word to search:");
        String word=scanner.nextLine();

        ArrayList<String> results=SearchService.searchWord(document.getText(),word);
        if(results.isEmpty()){
            System.out.println("word not found");
        }else{
            System.out.println("\nResults:");
            for(String line: results){
                System.out.println(line);
            }
            System.out.println("\nTotal Occurence: "+ results.size());
        }

    scanner.close();
    }
}
