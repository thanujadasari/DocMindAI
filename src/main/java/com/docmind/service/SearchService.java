package com.docmind.service;
import java.util.ArrayList;

public class SearchService {
    public static ArrayList<String> searchWord(String pdfText, String word){
        String[] lines=pdfText.split("\n");
        ArrayList<String> results=new ArrayList<>();
        for(String line : lines){
            if(line.toLowerCase().contains(word.toLowerCase())){
                results.add(line);
            }
        }

     return results;
    }
}

