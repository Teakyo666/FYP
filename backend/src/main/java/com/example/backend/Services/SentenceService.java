package com.example.backend.Services;

import com.example.backend.POJO.DO.SentenceDO;
import com.example.backend.POJO.Result;

public interface SentenceService {
    Result createSentence(SentenceDO sentenceDO);
    Result updateSentence(SentenceDO sentenceDO);
    Result deleteSentence(Long id);
    Result checkSentence(Long id);
    Result findSentence();
    Result listSentences(Integer page, Integer size, String sentence);
}
