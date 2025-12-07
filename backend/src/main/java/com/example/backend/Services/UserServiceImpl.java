package com.example.backend.Services;

import com.example.backend.Mappers.LoginMapper;
import com.example.backend.Mappers.UserInfoMapper;
import com.example.backend.POJO.DO.LoginDO;
import com.example.backend.POJO.DO.UserInfoDO;
import com.example.backend.POJO.Result;
import com.example.backend.Services.Tools.PasswordToken;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("UserService")
public class UserServiceImpl implements UserService {
    @Autowired
    private LoginMapper loginMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public Result createUser(LoginDO loginDO){
        Result result = new Result();
        String uuid = UUID.randomUUID().toString();
        loginDO.setId(uuid);
        String PasswordTK = PasswordToken.encode(loginDO.getPassword());
        loginDO.setPassword(PasswordTK);
        loginMapper.insert(loginDO);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("create user success");
        return result;
    }

    @Override
    public Result deleteUser(String id){
        Result result = new Result();
        loginMapper.deleteById(id);
        UserInfoDO userInfoDO= userInfoMapper.selectById(id);
        userInfoMapper.deleteById(userInfoDO.getId());
        result.setSuccess(Boolean.TRUE);
        result.setMessage("delete user success");
        return result;
    }

    @Override
    public Result updateUser(LoginDO loginDO){
        Result result = new Result();
        loginMapper.update(loginDO);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("update user success");
        return result;
    }

    @Override
    public Result getUser(String username){
        Result result = new Result();
        loginMapper.selectByUsername(username);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("get user success");
        return result;
    }

    @Override
    public Result listUsers(Integer page, Integer size, String keyword, String role) {
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 10;
        if (size > 50) size = 50;

        PageHelper.startPage(page, size);

        List<LoginDO> loginList;
        if (keyword != null && !keyword.trim().isEmpty()) {
            loginList = loginMapper.selectByCondition(keyword, role);
        } else if (role != null && !role.trim().isEmpty()) {
            loginList = loginMapper.selectByCondition(null, role);
        } else {
            loginList = loginMapper.selectAll();
        }

        PageInfo<LoginDO> pageInfo = new PageInfo<>(loginList);

        Result result = new Result();
        result.setSuccess(Boolean.TRUE);
        result.setMessage("Query success");

        Map<String, Object> data = new HashMap<>();
        data.put("list", pageInfo.getList());
        data.put("total", pageInfo.getTotal());
        data.put("page", pageInfo.getPageNum());
        data.put("size", pageInfo.getPageSize());
        
        result.setData(data);
        return result;
    }

}
