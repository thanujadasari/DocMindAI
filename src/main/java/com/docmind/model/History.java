package com.docmind.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;
    private LocalDateTime askedAt;
    public History() {
    }

    public History(String question, String answer, LocalDateTime askedAt){
        this.question=question;
        this.answer=answer;
        this.askedAt=askedAt;
    }

    public Long getId() {
        return id;
    }

    public String getQuestion(){
        return question;
    }

    public String getAnswer(){
        return answer;
    }

    public LocalDateTime getAskedAt(){
        return askedAt;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setAskedAt(LocalDateTime askedAt) {
        this.askedAt = askedAt;
    }

}
