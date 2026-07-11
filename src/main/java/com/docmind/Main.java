package com.docmind;
import com.docmind.utils.PdfReaderUtil;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        System.out.println("Welcome to DocMind AI");
        String pdfPath = "src/main/resources/Community Connect Form.pdf.pdf";
        String pdfText=PdfReaderUtil.readPdf(pdfPath);

        String[] lines=pdfText.split("\n");

        Scanner scanner= new Scanner(System.in);
        System.out.println("Enter a word to search:");
        String word=scanner.nextLine();

        boolean found=false;
        for(String line: lines){
            if(line.toLowerCase().contains(word.toLowerCase())){
                System.out.println(line);
                found=true;
            }
        }
        if(!found){
            System.out.println("Word not found.");
        }

    scanner.close();
    }
}
