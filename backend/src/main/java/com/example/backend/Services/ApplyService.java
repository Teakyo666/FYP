package com.example.backend.Services;

import com.example.backend.POJO.DO.ApplyDO;
import com.example.backend.POJO.Result;

public interface ApplyService {
    Result create(ApplyDO applyDO);
    Result update(ApplyDO applyDO);
    Result delete(String id);
    Result listApplies(Integer page, Integer size, String to_apply, String status);
}