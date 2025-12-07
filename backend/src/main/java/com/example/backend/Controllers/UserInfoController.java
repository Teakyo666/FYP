package com.example.backend.Controllers;

import com.example.backend.POJO.DO.UserInfoDO;
import com.example.backend.POJO.Result;
import com.example.backend.Services.UserInfoService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userInfo")
@Validated
public class UserInfoController {
    @Resource
    private UserInfoService userInfoService;

    @GetMapping("/get")
    public Result getUserInfo(@RequestParam String userId) {
        return userInfoService.getProfile(userId);
    }

    @PostMapping("update")
    public Result updateInfo(@RequestBody UserInfoDO userInfoDO) {
        return userInfoService.updateInfo(userInfoDO);
    }

}
