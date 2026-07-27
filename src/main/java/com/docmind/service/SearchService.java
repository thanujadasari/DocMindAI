package com.docmind.service;

import java.util.ArrayList;

public class SearchService {

    public static String searchAnswer(String pdfText, ArrayList<String> keywords) {

        String[] lines = pdfText.split("\\r?\\n");

        int maxMatches = 0;
        String bestLine = "";

        for (String line : lines) {

            int matches = 0;

            String lowerLine = line.toLowerCase();

            for (String keyword : keywords) {

                if (lowerLine.contains(keyword.toLowerCase())) {
                    matches++;
                }

            }

            if (matches > maxMatches) {
                maxMatches = matches;
                bestLine = line;
            }

        }

        if (maxMatches > 0) {
            return bestLine;
        }

        return "Answer not found.";
    }
}