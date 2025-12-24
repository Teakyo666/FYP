package com.example.backend.Services;

import com.example.backend.POJO.DO.UserInfoDO;
import com.example.backend.POJO.Result;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserInfoService {
    Result getProfile(String id);
    Result updateInfo(UserInfoDO userInfoDO);
    String saveImage(MultipartFile file) throws IOException;
}
