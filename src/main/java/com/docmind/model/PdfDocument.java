package com.docmind.model;

public class PdfDocument {
    private String fileName;
    private String text;
    public PdfDocument(String fileName, String text){
        this.fileName=fileName;
        this.text=text;
    }
    public String getFileName(){
        return fileName;


    }
    public String getText(){
        return text;
    }
}
