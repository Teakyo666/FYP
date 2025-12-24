package com.example.backend.Services;

import com.example.backend.POJO.DO.RecycleDO;
import com.example.backend.POJO.Result;

public interface RecycleService {
    Result CreatePlace (RecycleDO recycleDO);
    Result DeletePlace (String id);
    Result UpdatePlace(RecycleDO recycleDO);
    Result SearchPlace(String Country, String City);
    Result GetPlace(String id);
    Result listPlaces(Integer page, Integer size, String name);
}
