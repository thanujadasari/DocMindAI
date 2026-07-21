package com.docmind.service;
import java.util.ArrayList;

public class SearchService {
    public static ArrayList<String> searchKeywords(String pdfText, ArrayList<String> keywords) {
        String[] lines = pdfText.split("\n");

        ArrayList<String> results = new ArrayList<>();

        for (String line : lines) {

            boolean allKeywordsFound = true;

            for (String keyword : keywords) {

                if (!line.toLowerCase().contains(keyword.toLowerCase())) {
                    allKeywordsFound = false;
                    break;
                }
            }

            if (allKeywordsFound) {
                results.add(line);
            }
        }

        return results;
    }
}

