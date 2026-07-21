package com.docmind.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class QuestionService {

    private static final List<String> STOP_WORDS = Arrays.asList(
            "what","who","where","when","is","are",
            "the","a","an","of","to","for","in","my"
    );

    public static ArrayList<String> extractKeywords(String question){
        ArrayList<String> keywords=new ArrayList<>();
        String[] words= question.toLowerCase().split("\\s+");
        for(String word: words){
            word=word.replaceAll("[^a-zA-Z0-9]","");
            if(!word.isEmpty() && !STOP_WORDS.contains(word)){
                keywords.add(word);
            }
        }
        return keywords;
    }

}
