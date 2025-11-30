package com.example.backend.Services;

import com.example.backend.POJO.DO.GarbageDO;
import com.example.backend.POJO.Result;
import com.example.backend.POJO.VO.GarbageVO;

public interface GarbageService {
    Result createGarbage(GarbageVO garbageVO);
    Result deleteGarbage(String garbageId);
    Result updateGarbage(GarbageDO garbageDO);
    Result getGarbage(String garbageName);
}
