package com.example.backend.Services;

import com.example.backend.POJO.DO.UserInfoDO;
import com.example.backend.POJO.Result;

public interface UserInfoService {
    Result getProfile(String id);
    Result updateInfo(UserInfoDO userInfoDO);
}
