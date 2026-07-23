package com.docmind.service;

import com.docmind.model.History;
import com.docmind.repository.HistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class HistoryService {
    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository){
        this.historyRepository=historyRepository;
    }

    public void saveHistory(String question, String answer) {
        History history = new History(
                question,
                answer,
                LocalDateTime.now()
        );

        historyRepository.save(history);
    }
    }

