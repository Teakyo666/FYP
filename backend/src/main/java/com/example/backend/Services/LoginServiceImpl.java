package com.example.backend.Services;

import com.example.backend.Mappers.LoginMapper;
import com.example.backend.Mappers.PasswordResetMapper;
import com.example.backend.Mappers.UserInfoMapper;
import com.example.backend.POJO.*;
import com.example.backend.POJO.DO.LoginDO;
import com.example.backend.POJO.DO.UserInfoDO;
import com.example.backend.POJO.VO.ForgotPasswordVO;
import com.example.backend.POJO.VO.LoginRequestVO;
import com.example.backend.POJO.VO.PasswordResetDO;
import com.example.backend.POJO.VO.ResetPasswordVO;
import com.example.backend.units.PasswordToken;
import com.example.backend.units.TokenUtil;
import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("LoginService")
public class LoginServiceImpl implements LoginService {
    @Resource
    private LoginMapper loginMapper;
    @Resource
    private PasswordResetMapper passwordResetMapper;
    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public String createUser(LoginRequestVO loginRequestVO){
        LoginDO loginDO = new LoginDO();
        String uuid = UUID.randomUUID().toString();
        String Password = PasswordToken.encode(loginRequestVO.getPassword());
        loginDO.setId(uuid);
        loginDO.setUsername(loginRequestVO.getUsername());
        loginDO.setPassword(Password);
        loginDO.setRole("customer");
        loginMapper.insert(loginDO);

        // User information register
        String InfoId = UUID.randomUUID().toString();
        UserInfoDO userInfoDO = new UserInfoDO();
        userInfoDO.setId(InfoId);
        userInfoDO.setUserId(uuid);

        // Auto generate system name
        String cleanId = InfoId == null ? "" : InfoId.replace("-", "");
        String shortId = cleanId.substring(0, Math.min(8, cleanId.length()));
        String name = "User" + shortId + (int)(Math.random() * 100);
        userInfoDO.setName(name);
        userInfoDO.setAvatar("http://localhost:8080/assets/2025/12/avatar_524d83a5496f.png");
        userInfoMapper.insert(userInfoDO);
        return uuid;
    }

    public Result login(LoginRequestVO loginRequestVO){
        Result result = new Result();
        LoginDO login = loginMapper.selectByUsername(loginRequestVO.getUsername());
        if(login != null){
            boolean success = PasswordToken.matches(loginRequestVO.getPassword(),login.getPassword());
            if(success){
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("id", login.getId());
                dataMap.put("role", login.getRole());
                UserInfoDO userInfoDO = userInfoMapper.selectByUserId(login.getId());
                dataMap.put("name", userInfoDO.getName());
                dataMap.put("avatar", userInfoDO.getAvatar());
                result.setData(dataMap);
                result.setSuccess(Boolean.TRUE);
            }else{
                result.setSuccess(Boolean.FALSE);
                result.setMessage("Password is incorrect!");
            }
        }else{
            result.setSuccess(Boolean.FALSE);
            result.setMessage("Username not find!");
        }
        return result;
    }

    public Result applyResetPassword(ForgotPasswordVO dto){
        LoginDO loginDO = loginMapper.selectByUsername(dto.getUsername());
        Result result = new Result();
        if (loginDO == null) {
            result.setSuccess(Boolean.FALSE);
            result.setMessage("Username not find!");
            return result;
        }

        String resetToken = TokenUtil.generateResetToken();
        Date expireTime = new Date(System.currentTimeMillis() + 15 * 60 * 1000);

        // save in sql
        PasswordResetDO resetDO = new PasswordResetDO();
        resetDO.setId(UUID.randomUUID().toString().replace("-", ""));
        resetDO.setUsername(dto.getUsername());
        resetDO.setResetToken(resetToken);
        resetDO.setExpireTime(expireTime);
        resetDO.setIsUsed(0);
        passwordResetMapper.insertResetToken(resetDO);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("Get token success!");
        Map<String ,Object> dataMap = new HashMap<>();
        dataMap.put("token",resetToken);
        result.setData(dataMap);
        return result;
    }

    public Result confirmResetPassword(ResetPasswordVO dto) {
        PasswordResetDO resetDO = passwordResetMapper.selectByToken(dto.getResetToken());
        Result result = new Result();
        if (resetDO != null) {
            result.setSuccess(Boolean.TRUE);
            LoginDO loginDO = loginMapper.selectByUsername(resetDO.getUsername());
            loginDO.setPassword(PasswordToken.encode(dto.getNewPassword()));
            loginMapper.update(loginDO);
            result.setMessage("Password reset success!");
        }else{
            result.setSuccess(Boolean.FALSE);
            result.setMessage("Please get reset token first!");
        }
        return result;
    }
}
