package com.docmind.utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;


public class PdfReaderUtil {
    public static String readPdf(String filePath){
        try{
            PDDocument document = Loader.loadPDF(new File(filePath));
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            document.close();
            return text;


        }
        catch(IOException e){
            return null;

        }
    }
}
