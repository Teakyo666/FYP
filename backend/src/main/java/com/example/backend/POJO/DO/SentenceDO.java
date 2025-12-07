package com.example.backend.POJO.DO;

import java.time.LocalDateTime; // 改为LocalDateTime适配timestamp

public class SentenceDO {
    private Long id;
    private String sentence;
    private LocalDateTime createDate;
    private String createBy;

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

    public LocalDateTime getCreateDate() { // 驼峰命名：createDate
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getCreateBy() { // 驼峰命名：createBy
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }
}