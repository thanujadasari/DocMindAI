package com.docmind.service;

import java.util.ArrayList;

public class SearchService {

    public static String searchAnswer(String pdfText, ArrayList<String> keywords) {

        String[] lines = pdfText.split("\n");

        for (int i = 0; i < lines.length; i++) {

            String line = lines[i].toLowerCase();

            boolean allKeywordsFound = true;

            for (String keyword : keywords) {
                if (!line.contains(keyword.toLowerCase())) {
                    allKeywordsFound = false;
                    break;
                }
            }

            if (allKeywordsFound) {

                if (i + 1 < lines.length) {
                    return lines[i + 1];
                }
            }
        }

        return "Answer not found.";
    }
}