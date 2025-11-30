package com.example.backend.POJO.DO;

import java.sql.Timestamp;

public class SentenceDO {
    private Long id;
    private String sentence;
    private Timestamp used_date;
    private Timestamp created_date;
    private String created_by;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public Timestamp getUsed_date() {
        return used_date;
    }

    public void setUsed_date(Timestamp used_date) {
        this.used_date = used_date;
    }

    public Timestamp getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Timestamp created_date) {
        this.created_date = created_date;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }
}
