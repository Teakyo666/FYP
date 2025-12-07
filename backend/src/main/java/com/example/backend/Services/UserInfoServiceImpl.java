package com.example.backend.Services;

import com.example.backend.Mappers.UserInfoMapper;
import com.example.backend.POJO.DO.UserInfoDO;
import com.example.backend.POJO.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("UserInfoService")
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public Result getProfile(String Userid){
        Result result = new Result();
        UserInfoDO userInfoDO = userInfoMapper.selectByUserId(Userid);
        result.setData(userInfoDO);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("get Profile success");
        return result;
    }

    @Override
    public Result updateInfo(UserInfoDO userInfoDO){
        Result result = new Result();
        userInfoMapper.update(userInfoDO);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("update Profile success");
        return result;
    }

}
