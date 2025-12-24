package com.example.backend.Services;

import com.example.backend.POJO.DO.AIGenerateDO;
import com.example.backend.POJO.Result;

public interface AIGenerateService {
    Result create(AIGenerateDO aiGenerateDO);
    Result update(AIGenerateDO aiGenerateDO);
    Result delete(String id);
    Result listAIGarbage(Integer page, Integer size, String keyword, String type, String status);
}

