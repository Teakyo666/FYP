package com.example.backend.Services;

import com.example.backend.POJO.DO.LoginDO;
import com.example.backend.POJO.Result;

public interface UserService {
    Result createUser(LoginDO loginDO);
    Result deleteUser(String id);
    Result updateUser(LoginDO loginDO);
    Result listUsers(Integer page, Integer size, String keyword, String role);
    Result getUser(String username);
}
