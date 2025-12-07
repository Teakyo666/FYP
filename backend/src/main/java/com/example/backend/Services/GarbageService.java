package com.example.backend.Services;

import com.example.backend.POJO.DO.GarbageDO;
import com.example.backend.POJO.Result;


public interface GarbageService {
    Result createGarbage(GarbageDO garbageDO);
    Result deleteGarbage(String garbageId);
    Result updateGarbage(GarbageDO garbageDO);
    Result getGarbage(String garbageName);
    Result listGarbage(int page, int size, String keyword, String type);
}
